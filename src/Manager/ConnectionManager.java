package Manager;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;

public class ConnectionManager {
    public static void broadcast(JsonObject message) throws IOException {
        System.out.println("Broadcasting: " + message.toString());
        String messageString = new Gson().toJson(message);
        for (Connection st : Server.connections) {
            if (!st.socket.isClosed()) {
                st.dataOutputStream.writeUTF(messageString);
                st.dataOutputStream.flush();
            }
        }
    }
    public static void broadcastBatch(ArrayList<JsonObject> recordList) throws IOException {
        for (JsonObject message : recordList) {
            String messageString = new Gson().toJson(message);
            for (Connection st : Server.connections) {
                st.dataOutputStream.writeUTF(messageString);
                st.dataOutputStream.flush();
            }
        }
    }

    public static void addUsers(JsonObject userListJson) {
        // Extracting user list from the JSON object
        JsonArray users = userListJson.getAsJsonArray("usernames");

        // Synchronize the server's user list with the received list
        Server.users.clear();
        for (JsonElement user : users) {
            Server.users.add(user.getAsString());
        }

        // Optionally
        try {
            broadcast(userListJson);
        } catch (Exception e) {
            System.out.println("Failed to broadcast user list");
        }
    }

    public static int checkin(String curName) {
        System.out.println("Checking in: " + curName);
        return LoginBoard.createMyBoard.showRequest(curName);
    }

    public static void clientOut(JsonObject data) {
        String curName = data.get("username").getAsString();
        Server.users.remove(curName);
        Server.connections.removeIf(conn -> conn.name.equals(curName));

        System.out.println("User disconnected: " + curName);

    }

    public static void canvasRepaint(JsonObject drawRecord) {
        System.out.println("Repainting canvas"+drawRecord.toString());
        ManagerBoard.createBoardListener.update(drawRecord);
        ManagerBoard.canvas.repaint();
    }
}
