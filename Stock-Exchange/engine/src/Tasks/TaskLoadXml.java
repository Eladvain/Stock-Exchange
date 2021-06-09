package Tasks;

import GenratedCode.RizpaStockExchangeDescriptor;
import components.commerce.RitzpaStockManager;
import components.dataTransferObject.JaxbXmlToObject;
import components.dataTransferObject.ObjectSerialization;
import javafx.concurrent.Task;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.UnmarshalException;

public class TaskLoadXml extends Task<Boolean> {
    public File filePath;
    public boolean isXmlLoader;
    public StringBuilder errorDesc;
    public RitzpaStockManager manager;

    public TaskLoadXml(File FilePath, boolean xmlLoaderBool) {
        isXmlLoader = xmlLoaderBool;
        errorDesc = new StringBuilder();
        this.filePath = FilePath;
    }

    @Override
    protected Boolean call() throws Exception {
        try {
            if (isXmlLoader) {
                updateMessage("loading xml file");
                updateProgress(0.33, 1);
                Thread.sleep(1000);
                RizpaStockExchangeDescriptor rtz = JaxbXmlToObject.JaxbXml2Object(filePath.getPath().toString());
                manager = RitzpaStockManager.createFromGenCode(rtz);
                updateMessage("Jaxb to object from xml file succeeded");
                updateProgress(0.66, 1);
                Thread.sleep(1000);
                if (JaxbXmlToObject.isXmlValid(rtz, errorDesc)) {
                    updateMessage("xml validation check was finished successfully");
                    updateProgress(1, 1);
                    Thread.sleep(1000);
                    return Boolean.TRUE;
                } else {
                    updateMessage("xml validation check was failed ");
                    updateProgress(0, 1);
                    Thread.sleep(1000);
                    return Boolean.FALSE;
                }
            } else {
                updateMessage("Last system state reloading...");
                updateProgress(0.5, 1);
                Thread.sleep(1000);
                manager = ObjectSerialization.reloadSystemState();
                if (manager == null) {
                    updateMessage("Last system state reload was failed");
                    updateProgress(0, 1);
                    Thread.sleep(1000);
                    return Boolean.FALSE;
                } else {
                    updateMessage("Last system state reloaded successfully");
                    updateProgress(1, 1);
                    Thread.sleep(1000);
                    return Boolean.TRUE;
                }
            }
        } catch (FileNotFoundException | NullPointerException fnfex) {
            errorDesc.append("File  Not Found");
            return Boolean.FALSE;
        } catch (JAXBException jaxbex) {
            errorDesc.append("Error-File is not Exist in the requested path");
            return Boolean.FALSE;
        } catch (Exception ex) {
            errorDesc.append("Error in loading xml file");
            return Boolean.FALSE;
        }

    }

}

