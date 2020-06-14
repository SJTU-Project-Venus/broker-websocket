package macoredroid;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
@Component
@ServerEndpoint("/websocket/{traderId}")
public class WebSocket {
    Logger logger = LoggerFactory.getLogger(WebSocket.class);
    private static Map<String, WebSocket> clients = new ConcurrentHashMap<>();
    private String traderId;
    private Session session;

    @OnOpen
    public void onOpen(@PathParam("traderId") String traderId, Session session){
        logger.info("connect {}", traderId);
        this.traderId = traderId;
        this.session = session;
        clients.put(traderId, this);
    }

    @OnError
    public void onError(Session session, Throwable error){
        System.out.println(error.getMessage());
    }

    @OnClose
    public void onClose(){
        clients.remove(traderId);
    }

    public String getTraderId()
    {
        return this.traderId;
    }

    public static void sendMessage(String message, int type) throws IOException{
        if(type==0) {//marketdepth
            for (WebSocket item : clients.values()) {
                item.session.getBasicRemote().sendText(message);
            }
        }
        else
        {
            JSONObject jsonObject;
            try {
                jsonObject = (JSONObject) JSON.parse(message);
                JSONObject order= (JSONObject) JSON.parse((String)jsonObject.get("order"));
                String buyerTraderName= (String) order.get("buyerTraderName");
                String sellerTraderName= (String) order.get("sellerTraderName");
                order.put("buyerTraderName","");
                order.put("buyerTraderDetailName","");
                order.put("sellerTraderName","");
                order.put("sellerTraderDetailName","");
                jsonObject.put("order",order.toJSONString());
                String another=jsonObject.toJSONString();
                for (WebSocket item : clients.values()) {
                    if(item.getTraderId().equals(buyerTraderName)||item.getTraderId().equals(sellerTraderName)||item.getTraderId().equals("broker-ui"))
                    {
                        item.session.getBasicRemote().sendText(message);
                    }
                    else {
                        item.session.getBasicRemote().sendText(another);
                    }
                }
            }catch (JSONException ignored){

            }

        }
    }
}
