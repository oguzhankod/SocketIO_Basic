package com.websocket.websocketbasic.socket;

import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.websocket.websocketbasic.model.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SocketModule {

    private final SocketIOServer socketIOServer;

    public SocketModule(SocketIOServer socketIOServer) {
        this.socketIOServer = socketIOServer;
        socketIOServer.addConnectListener(onConnected());
        socketIOServer.addDisconnectListener(onDisconnected());
        //custom event
        socketIOServer.addEventListener("send-message", Message.class,onMessageListener());
        //send-message tetiklensin
    }

    private DataListener<Message> onMessageListener(){
       //gonderen-mesaj-alan
        return (senderClient,data,ackRequest) -> {
            log.info(String.format("%s -> %s ",senderClient.getSessionId(),data.getContent()));
            // senderClient.getNamespace().getBroadcastOperations().sendEvent("get-message",data); // get-messageye yollanacak
            senderClient.getNamespace().getAllClients().forEach(x->{
                    if(!x.getSessionId().equals(senderClient.getSessionId())) {
                        x.sendEvent("get-message",data);
                    }
        });

        };
    }

    private ConnectListener onConnected(){
        return client -> {
            log.info("Socket Io connected : "+ client.getSessionId().toString());
        };

    }

    private DisconnectListener onDisconnected(){
        return cliet -> {
            log.info("Socket Io disconnected : "+ cliet.getSessionId().toString());
        };
    }
}
