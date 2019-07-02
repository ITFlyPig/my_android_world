package com.wangyuelin.performance.socket;


import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;
import com.neovisionaries.ws.client.WebSocketListener;
import com.wangyuelin.myandroidworld.util.LogUtil;

import java.io.IOException;
import java.util.List;
import java.util.Map;


public class WebSocketHelper {
    private WebSocket mWebSocket;
    private SocketListener mWebSocketListener;

    private static class Holder {
        private static WebSocketHelper webSocketHelper = new WebSocketHelper();
    }

    private WebSocketHelper() {
    }

    public static WebSocketHelper getInstance() {
        return Holder.webSocketHelper;
    }

    /**
     * 开始连接
     */
    public void connect(String sid) {
        try {
            mWebSocket = new WebSocketFactory()
                    .setConnectionTimeout(3000)
                    .createSocket("ws://172.16.128.171:8080/websocket/" + sid)
                    .setFrameQueueSize(10)
                    .setMissingCloseFrameAllowed(false)
                    .addListener(webSocketListener)
                    .connectAsynchronously();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void stop() {
        if (mWebSocket != null) {
            mWebSocket.disconnect();
        }
    }

    public void send(String msg) {
        if (mWebSocket != null) {
            mWebSocket.sendText(msg);
        }
    }

    public interface SocketListener {
        void onMessage(String msg);
    }

    private WebSocketListener webSocketListener = new WebSocketAdapter() {

        @Override
        public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
            super.onConnected(websocket, headers);
            LogUtil.e("websocket", "onConnected");
        }

        @Override
        public void onConnectError(WebSocket websocket, WebSocketException exception) throws Exception {
            super.onConnectError(websocket, exception);
            LogUtil.e("websocket", "连接错误：" + exception.getLocalizedMessage());
        }

        @Override
        public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
            super.onDisconnected(websocket, serverCloseFrame, clientCloseFrame, closedByServer);
            LogUtil.e("websocket", "onDisconnected");
        }

        @Override
        public void onTextMessage(WebSocket websocket, String text) throws Exception {
            super.onTextMessage(websocket, text);
            if (mWebSocketListener != null) {
                mWebSocketListener.onMessage(text);
            }
        }
    };

    public void setWebSocketListener(SocketListener webSocketListener) {
        this.mWebSocketListener = webSocketListener;
    }
}
