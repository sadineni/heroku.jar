package com.heroku.api.command;

import com.google.inject.Inject;
import com.heroku.api.ConnectionTestModule;
import com.heroku.api.HerokuRequestKey;
import com.heroku.api.HerokuStack;
import com.heroku.api.exception.HerokuAPIException;
import com.heroku.api.connection.Connection;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.KeyPair;
import org.apache.commons.io.FileUtils;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * TODO: Javadoc
 *
 * @author Naaman Newbold
 */
@Guice(modules = ConnectionTestModule.class)
public class NoAppCommandIntegrationTest {

    private static final String PUBLIC_KEY_COMMENT = "foo@bar";

    @Inject
    Connection connection;

    // doesn't need an app
    @Test
    public void testKeysAddCommand() throws IOException, HerokuAPIException, JSchException {
        JSch jsch = new JSch();
        KeyPair keyPair = KeyPair.genKeyPair(jsch, KeyPair.RSA);

        ByteArrayOutputStream publicKeyOutputStream = new ByteArrayOutputStream();
        keyPair.writePublicKey(publicKeyOutputStream, PUBLIC_KEY_COMMENT);
        publicKeyOutputStream.close();
        String sshPublicKey = new String(publicKeyOutputStream.toByteArray());

        CommandConfig config = new CommandConfig()
                .onStack(HerokuStack.Cedar)
                .with(HerokuRequestKey.sshkey, sshPublicKey);

        Command cmd = new KeysAddCommand(config);
        CommandResponse response = connection.executeCommand(cmd);

        assertTrue(response.isSuccess());
    }

    // doesn't need an app
    @Test(dependsOnMethods = {"testKeysAddCommand"})
    public void testKeysRemoveCommand() throws IOException, HerokuAPIException {
        CommandConfig config = new CommandConfig()
                .onStack(HerokuStack.Cedar)
                .with(HerokuRequestKey.name, PUBLIC_KEY_COMMENT);

        Command cmd = new KeysRemoveCommand(config);
        CommandResponse response = connection.executeCommand(cmd);

        assertTrue(response.isSuccess());
    }

    // doesn't need an app
    // currently uses a key associated with another user but really should do the following:
    // add a key to one user, then try to add the same key to another user
    // but this depends on having two users in auth-test.properties
    @Test
    public void testKeysAddCommandWithDuplicateKey() throws IOException, HerokuAPIException {
        String sshkey = FileUtils.readFileToString(new File(getClass().getResource("/id_rsa.pub").getFile()));

        CommandConfig config = new CommandConfig()
                .onStack(HerokuStack.Cedar)
                .with(HerokuRequestKey.sshkey, sshkey);

        Command cmd = new KeysAddCommand(config);
        CommandResponse response = connection.executeCommand(cmd);

        assertFalse(response.isSuccess());
    }

}