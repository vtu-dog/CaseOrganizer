package app.ftpconn;

import it.sauronsoftware.ftp4j.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.DatatypeConverter;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.io.File;
import java.io.*;

import app.xml.BasicCase;


public class FTPConn {

    protected final String ftpDir = "CaseOrganizer";
    private FTPClient client = null;

    public FTPConn (String addr, String login, String passwd) throws Exception {
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

    private String getMD5 (String msg) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(msg.getBytes());
            byte[] digest = md.digest();
            return DatatypeConverter.printHexBinary(digest).toUpperCase();
        } catch (NoSuchAlgorithmException e) { return null; } // should NEVER happen
    }

    private void uploadMetadata (BasicCase caseObj) throws Exception {
        JAXBContext jaxbContext = JAXBContext.newInstance(BasicCase.class);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        ByteArrayOutputStream metaFile = new ByteArrayOutputStream();
        jaxbMarshaller.marshal(caseObj, metaFile);

        try {
            client.deleteFile("meta.xml");
        } catch (FTPException e) { }

        client.upload("meta.xml", new ByteArrayInputStream(metaFile.toByteArray()), 0, 0, null);
    }

    public void closeConn () throws Exception {
        if (client != null) {
            client.disconnect(true);
            client = null;
        }
    }

    public List<String> listDirs () throws Exception {
        List<String> outputList = new ArrayList<String>();

        FTPFile[] serverList = client.list();
        for (FTPFile file : serverList)
            outputList.add(file.getName());
        return outputList;
    }

    public BasicCase readMetadata (String letterNumber) throws Exception {
        try {
            String caseHash = getMD5(letterNumber);
            client.changeDirectory(caseHash);

            ByteArrayOutputStream metaFile = new ByteArrayOutputStream();
            client.download("meta.xml", metaFile, 0, null);

            JAXBContext jaxbContext = JAXBContext.newInstance(BasicCase.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            return (BasicCase) jaxbUnmarshaller.unmarshal(new ByteArrayInputStream(metaFile.toByteArray()));
        }
        catch (Exception e) { throw e; }
        finally { client.changeDirectory("/"); }
    }

    public void createCase (String letterNumber) throws Exception {
        try {
            String caseHash = getMD5(letterNumber);

            client.createDirectory(caseHash);
            client.changeDirectory(caseHash);

            BasicCase newCase = new BasicCase();
            newCase.setLetterNumber(letterNumber);

            uploadMetadata(newCase);
        }
        catch (Exception e) { throw e; }
        finally { client.changeDirectory("/"); }

    }

    public void deleteCase (String caseName) throws Exception {
        try {
            client.changeDirectory(caseName);

            for (FTPFile file : client.list())
                client.deleteFile(file.getName());

            client.changeDirectory("/");
            client.deleteDirectory(caseName);
        }
        catch (Exception e) { throw e; }
        finally { client.changeDirectory("/"); }
    }

    public ByteArrayOutputStream downloadFile (String filename) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        client.download(filename, baos, 0, null);
        return baos;
    }

}
