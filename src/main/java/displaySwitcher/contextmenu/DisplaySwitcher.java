/*
 * Copyright (c) 2023. PortSwigger Ltd. All rights reserved.
 *
 * This code may be used to extend the functionality of Burp Suite Community Edition
 * and Burp Suite Professional, provided that this usage does not violate the
 * license terms for those products.
 */

package displaySwitcher.contextmenu;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.logging.Logging;
import burp.api.montoya.extension.ExtensionUnloadingHandler;

public class DisplaySwitcher implements BurpExtension
{
    private Logging logging;
    @Override
    public void initialize(MontoyaApi api)
    {
        api.extension().setName("DisplaySwitcher");
        
        
        logging = api.logging();
        api.userInterface().registerContextMenuItemsProvider(new MyContextMenuItemsProvider(api));
        
        DisplaySwitcherPanel fontSWITCHER = new DisplaySwitcherPanel(api);
        api.userInterface().registerSuiteTab("DisplaySwitcher", fontSWITCHER);

        api.extension().registerUnloadingHandler(new MyExtensionUnloadHandler());
    }
    private class MyExtensionUnloadHandler implements ExtensionUnloadingHandler {
		@Override
	public void extensionUnloaded() {
            logging.logToOutput("Extension was unloaded.");
	}
    }
}
