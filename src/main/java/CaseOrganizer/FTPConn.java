package app.ftpconn;

import it.sauronsoftware.ftp4j.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.JAXBException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.util.List;
import java.util.ArrayList;
import java.io.*;

import app.xml.BasicCase;


public class FTPConn {

    protected final String ftpDir = "/CaseOrganizer/";
    private FTPClient client = null;

    private String addr;
    private String login;
    private String passwd;

    private long noop = 20000;

    public FTPConn (String addr, String login, String passwd) throws IOException, FTPIllegalReplyException, FTPException {
        client = new FTPClient();
        this.addr = addr;
        this.login = login;
        this.passwd = passwd;

        client.connect(addr);
        client.login(login, passwd);
        client.setAutoNoopTimeout(noop);

        try {
            client.changeDirectory(ftpDir);
        } catch (FTPException ignore) {
            client.createDirectory(ftpDir);
            client.changeDirectory(ftpDir);
        }
    }

    private void relog () throws Exception {
        try {
            client.noop();
        } catch (Exception ignore) {
            try {
                client = new FTPClient();
                client.connect(addr);
                client.login(login, passwd);
                client.setAutoNoopTimeout(noop);
                client.changeDirectory(ftpDir);
            } catch (Exception e) { throw e; }
        }
    }

    public void closeConn () {
        try {
            if (client != null) {
                client.disconnect(true);
                client = null;
            }
        } catch (Exception ignore) { }
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
        relog();
        JAXBContext jaxbContext = JAXBContext.newInstance(BasicCase.class);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        ByteArrayOutputStream metaFile = new ByteArrayOutputStream();
        jaxbMarshaller.marshal(caseObj, metaFile);

        try {
            client.deleteFile("meta.xml");
        } catch (FTPException ignore) { }

        client.upload("meta.xml", new ByteArrayInputStream(metaFile.toByteArray()), 0, 0, null);
    }

    public void replaceMetadata (BasicCase caseObj) throws Exception {
        relog();
        client.changeDirectory(ftpDir + getMD5(caseObj.getLetterNumber()));
        uploadMetadata(caseObj);
        client.changeDirectory(ftpDir);
    }

    public List<String> listDirs () throws Exception {
        relog();

        List<String> outputList = new ArrayList<String>();
        FTPFile[] serverList = client.list();

        for (FTPFile file : serverList)
            outputList.add(file.getName());
        return outputList;
    }

    public BasicCase readMetadata (String caseHash) throws Exception {
        relog();
        client.changeDirectory(caseHash);

        ByteArrayOutputStream metaFile = new ByteArrayOutputStream();
        client.download("meta.xml", metaFile, 0, null);

        JAXBContext jaxbContext = JAXBContext.newInstance(BasicCase.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        client.changeDirectory(ftpDir);
        return (BasicCase) jaxbUnmarshaller.unmarshal(new ByteArrayInputStream(metaFile.toByteArray()));
    }

    public void createCase (String letterNumber) throws Exception {
        relog();
        String caseHash = getMD5(letterNumber);

        client.createDirectory(caseHash);
        client.changeDirectory(caseHash);

        BasicCase newCase = new BasicCase();
        newCase.setLetterNumber(letterNumber);

        uploadMetadata(newCase);
        client.changeDirectory(ftpDir);
    }

    public void deleteCase (BasicCase caseObj) throws Exception {
        relog();
        String caseName = getMD5(caseObj.getLetterNumber());
        client.changeDirectory(caseName);

        for (FTPFile file : client.list())
            client.deleteFile(file.getName());

        client.changeDirectory(ftpDir);
        client.deleteDirectory(caseName);
    }

    public ByteArrayOutputStream downloadFile (String filename) throws Exception {
        relog();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        client.download(filename, baos, 0, null);
        return baos;
    }

}
