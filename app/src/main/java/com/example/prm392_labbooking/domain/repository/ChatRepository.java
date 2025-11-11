package com.example.prm392_labbooking.domain.repository;

import com.example.prm392_labbooking.presentation.chat.ChatMessage;
import java.util.List;

public interface ChatRepository {
    void sendMessage(String userId, String message);
    void listenForMessages(String userId, MessageListener listener);
    void saveChatHistory(String userId, List<ChatMessage> messages, SaveListener listener);
    void loadChatHistory(String userId, ChatHistoryListener listener);

    interface MessageListener {
        void onNewMessages(List<String> messages);
    }
    interface SaveListener {
        void onSaved();
        void onError(Exception e);
    }
    interface ChatHistoryListener {
        void onHistoryLoaded(List<ChatMessage> messages);
        void onError(Exception e);
    }
}
