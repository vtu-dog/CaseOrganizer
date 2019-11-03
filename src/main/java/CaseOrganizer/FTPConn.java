package app.ftpconn;

import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPFile;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.DatatypeConverter;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import app.xml.BasicCase;


public class FTPConn {

    protected final String ftpDir = "/CaseOrganizer/";
    private FTPClient client = null;

    private String addr;
    private String login;
    private String passwd;

    private long noop = 20000;

    public FTPConn (String addr, String login, String passwd) throws Exception {
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
            }
            catch (Exception e) {
                throw e;
            }
            finally {
                client.changeDirectory(ftpDir);
            }
        }
    }

    public void noop () throws Exception {
        relog();
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
        try {
            relog();
            client.changeDirectory(ftpDir + getMD5(caseObj.getLetterNumber()));
            uploadMetadata(caseObj);
        }
        catch (Exception e) {
            throw e;
        }
        finally {
            client.changeDirectory(ftpDir);
        }
    }

    public List<String> listDirs (String path) throws Exception {
        try {
            relog();

            if (path != null)
                client.changeDirectory(ftpDir + getMD5(path));

            List<String> outputList = new ArrayList<String>();
            FTPFile[] serverList = client.list();

            for (FTPFile file : serverList)
                outputList.add(file.getName());
            return outputList;
        }
        catch (Exception e) {
            throw e;
        }
        finally {
            client.changeDirectory(ftpDir);
        }
    }

    public BasicCase readMetadata (String caseHash) throws Exception {
        try {
            relog();
            client.changeDirectory(caseHash);

            ByteArrayOutputStream metaFile = new ByteArrayOutputStream();
            client.download("meta.xml", metaFile, 0, null);

            JAXBContext jaxbContext = JAXBContext.newInstance(BasicCase.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            return (BasicCase) jaxbUnmarshaller.unmarshal(new ByteArrayInputStream(metaFile.toByteArray()));
        }
        catch (Exception e) {
            throw e;
        }
        finally {
            client.changeDirectory(ftpDir);
        }
    }

    public void createCase (String letterNumber) throws Exception {
        try {
            relog();
            String caseHash = getMD5(letterNumber);

            client.createDirectory(caseHash);
            client.changeDirectory(caseHash);

            BasicCase newCase = new BasicCase();
            newCase.setLetterNumber(letterNumber);

            uploadMetadata(newCase);
        }
        catch (Exception e) {
            throw e;
        }
        finally {
            client.changeDirectory(ftpDir);
        }
    }

    public void deleteCase (BasicCase caseObj) throws Exception {
        try {
            relog();
            String caseName = getMD5(caseObj.getLetterNumber());
            client.changeDirectory(caseName);

            for (FTPFile file : client.list())
                client.deleteFile(file.getName());

            client.changeDirectory(ftpDir);
            client.deleteDirectory(caseName);
        }
        catch (Exception e) {
            client.changeDirectory(ftpDir);
        }
    }

    public void uploadFile (File f, String dir) throws Exception {
        relog();
        InputStream targetStream = new FileInputStream(f);
        try {
            client.deleteFile(ftpDir + getMD5(dir) + "/" + f.getName());
        } catch (Exception ignore) { }
        client.upload(ftpDir + getMD5(dir) + "/" + f.getName(), targetStream, 0, 0, null);
    }

    public ByteArrayOutputStream downloadFile (String dir, String filename) throws Exception {
        relog();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        client.download(ftpDir + getMD5(dir) + "/" + filename, baos, 0, null);
        return baos;
    }

    public void deleteFile (String dir, String filename) throws Exception {
        relog();
        client.deleteFile(ftpDir + getMD5(dir) + "/" + filename);
    }

}
