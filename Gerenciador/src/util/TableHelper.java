package util;

public class TableHelper {
    
    private String id;

    private String host_name;

    private Integer port;

    private Boolean isServer;

    public TableHelper(String id, String host_name, Integer port, Boolean isServer) {
        this.id = id;
        this.host_name = host_name;
        this.port = port;
        this.isServer = isServer;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHost_name() {
        return host_name;
    }

    public void setHost_name(String host_name) {
        this.host_name = host_name;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Boolean getIsServer() {
        return isServer;
    }

    public void setIsServer(Boolean isServer) {
        this.isServer = isServer;
    }
    
}
