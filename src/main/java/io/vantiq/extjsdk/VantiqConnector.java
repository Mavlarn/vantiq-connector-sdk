package io.vantiq.extjsdk;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;

import static io.vantiq.extjsdk.ConnectorConstants.CONNECTOR_CONNECT_TIMEOUT;
import static io.vantiq.extjsdk.ConnectorConstants.RECONNECT_INTERVAL;

public abstract class VantiqConnector implements Closeable {

    private static final Logger LOG = LoggerFactory.getLogger(VantiqConnector.class);

    protected ExtensionWebSocketClient vantiqClient;
    protected ConnectorConfig connectionInfo;

    public VantiqConnector() {
        connectionInfo = new ConnectorConfig();
        if (connectionInfo == null) {
            throw new RuntimeException("No VANTIQ connection information provided");
        }
        if (connectionInfo.getSourceName() == null) {
            throw new RuntimeException("No source name provided");
        }

        vantiqClient = new ExtensionWebSocketClient(connectionInfo.getSourceName());
        vantiqClient.setAutoReconnect(true);

        init();
    }

    abstract public void init();

    public void start() {
        boolean sourcesSucceeded = false;
        while (!sourcesSucceeded) {
            vantiqClient.initiateFullConnection(connectionInfo.getVantiqUrl(), connectionInfo.getToken());

            sourcesSucceeded = vantiqClient.checkConnectionFails(CONNECTOR_CONNECT_TIMEOUT);
            if (!sourcesSucceeded) {
                try {
                    Thread.sleep(RECONNECT_INTERVAL);
                } catch (InterruptedException e) {
                    LOG.error("An error occurred when trying to sleep the current thread. Error Message: ", e);
                }
            }
        }
    }

//    @Override
//    public void close() {
//        if (this.vantiqClient.isOpen()) {
//            LOG.info("vantiqClient is to close:{}", this.vantiqClient);
//            this.vantiqClient.close();
//        }
//    }

    public ExtensionWebSocketClient getVantiqClient() {
        return vantiqClient;
    }

    public ConnectorConfig getConnectionInfo() {
        return connectionInfo;
    }
}
