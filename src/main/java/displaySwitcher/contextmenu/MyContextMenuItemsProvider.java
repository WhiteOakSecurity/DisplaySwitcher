/*
 * Copyright (c) 2023. PortSwigger Ltd. All rights reserved.
 *
 * This code may be used to extend the functionality of Burp Suite Community Edition
 * and Burp Suite Professional, provided that this usage does not violate the
 * license terms for those products.
 */

package displaySwitcher.contextmenu;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.core.ToolType;
import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import burp.api.montoya.ui.contextmenu.ContextMenuItemsProvider;
import burp.api.montoya.persistence.PersistedObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

public class MyContextMenuItemsProvider implements ContextMenuItemsProvider
{

    private final MontoyaApi api;
    public PersistedObject myExtensionData;

    public MyContextMenuItemsProvider(MontoyaApi api)
    {

        this.api = api;
        
    }

    @Override
    public List<Component> provideMenuItems(ContextMenuEvent event)
    {
        if (event.isFromTool(ToolType.PROXY, ToolType.TARGET, ToolType.LOGGER))
        {
            List<Component> menuItemList = new ArrayList<>();
            JSONArray rulesArray = new JSONArray(); //an array of JSON objects representing single rules
            myExtensionData = api.persistence().extensionData();
            
            for(String fontProfile : myExtensionData.stringKeys()) {
            if (!fontProfile.contains(":")){
                JMenuItem newItem = new JMenuItem(fontProfile); //adjust regex backslashes for display
                newItem.addActionListener(new ActionListener(){
                    public void actionPerformed(ActionEvent e){

                        loadFontProfile(fontProfile); 
                    }
                });

                menuItemList.add(newItem);
            }
           }
            
            
            String defaultFontProfile = "{\"user_options\":{\"display\":{\"character_sets\":{\"mode\":\"recognize_automatically\"},\"html_rendering\":{\"allow_http_requests\":true},\"http_message_display\":{\"font_name\":\"Monospaced\",\"font_size\":13,\"font_smoothing\":false,\"highlight_requests\":true,\"highlight_responses\":true,\"pretty_print_by_default\":true},\"table_appearance\":{\"zebra_striping\":true},\"user_interface\":{\"font_size\":13,\"look_and_feel\":\"Light\"}}}}";
            
            JMenuItem newItem = new JMenuItem("Default (13pt Monospaced)");
            newItem.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    api.burpSuite().importUserOptionsFromJson(defaultFontProfile);
                }
            });
            menuItemList.add(newItem);
          
            return menuItemList;
        }

        return null; //not Proxy, Logger, or Target so return null
    }
    
        
    public void saveNewFontProfile(String name){

        String userOptions = api.burpSuite().exportUserOptionsAsJson("user_options.display");
        api.logging().logToOutput("Exported Display Options: " + userOptions);
        
        myExtensionData = api.persistence().extensionData();
        myExtensionData.setString(name,userOptions);
        
    }
    
    public void loadFontProfile(String name){
        myExtensionData = api.persistence().extensionData();
        String fontProfile = myExtensionData.getString(name);
        api.burpSuite().importUserOptionsFromJson(fontProfile);
        
    }
}

/* Burp's export format, having used Paste-add: 

{
    "proxy":{
        "ssl_pass_through":{
            "rules":[
                {
                    "enabled":true,
                    "file":"^/log.*",
                    "host":"^play\\.google\\.com$",
                    "port":"^443$",
                    "protocol":"https"
                }
            ]
        }
    }
}
*/
