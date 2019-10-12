package app.ftpconn;

import it.sauronsoftware.ftp4j.*;

import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.io.File;
import java.io.*;
//import org.apache.commons.io.IOUtils;
import java.nio.charset.StandardCharsets;


public class FTPConn {

    protected final String ftpDir = "CaseOrganizer";
    private FTPClient client = null;

    public FTPConn (String addr, String login, String passwd) throws Exception {
        try {
            client = new FTPClient();
            client.connect(addr);
            client.login(login, passwd);
            client.setAutoNoopTimeout(30000);

            try {
                client.changeDirectory(ftpDir);
            } catch (FTPException e) {
                client.createDirectory(ftpDir);
                client.changeDirectory(ftpDir);
            }
        }
        catch (Exception e) { throw e; }
    }

    public void closeConn () throws Exception {
        if (client != null) {
            client.disconnect(true);
            client = null;
        }
    }

    public List<String> listDirs () throws Exception {
        List<String> outputList = new ArrayList<String>();

        FTPFile[] serverList = null;

        try {
            serverList = client.list();
            for (FTPFile file : serverList)
                outputList.add(file.getName());
            return outputList;
        }
        catch (Exception e) { throw e; }
    }

    public void readRootMetadata () throws Exception {
        ByteArrayOutputStream s = new ByteArrayOutputStream();
        client.download("meta.xml", s, 0, null);
        System.out.println(s.toString()); // TODO: return and analyze in the caller class
    }

    public void createCase (String caseName) throws Exception {
        client.createDirectory(caseName);
        client.changeDirectory(caseName);
        // TODO: create meta.xml
        client.changeDirectory("..");
    }

    public void deleteCase (String caseName) throws Exception {
        client.changeDirectory(caseName);

        for (FTPFile file : client.list())
            client.deleteFile(file.getName());

        client.changeDirectory("..");
        client.deleteDirectory(caseName);
    }

    public void downloadFile (String filename) throws Exception {
        ByteArrayOutputStream s = new ByteArrayOutputStream();
        client.download(filename, s, 0, null);
        // TODO: notify the caller to invoke a file picker
    }

}
