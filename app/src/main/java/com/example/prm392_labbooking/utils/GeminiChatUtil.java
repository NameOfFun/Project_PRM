package com.example.prm392_labbooking.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.prm392_labbooking.presentation.chat.ChatAdapter;
import com.example.prm392_labbooking.presentation.chat.ChatMessage;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class GeminiChatUtil {
    public interface GeminiStreamCallback {
        void onStreamUpdate(String text);
        void onError(String errorMsg);
        void onStreamComplete();
    }

    public static void streamGeminiResponse(Context context, List<ChatMessage> chatMessages, String userMessage, GeminiStreamCallback callback) {
        new Thread(() -> {
            final Handler mainHandler = new Handler(Looper.getMainLooper());
            try {
                String apiKey = SecretLoader.getGeminiApiKey(context);
                if (apiKey == null || apiKey.isEmpty()) {
                    mainHandler.post(() -> callback.onError("Lỗi: Chưa cấu hình API key cho Gemini. Vui lòng thêm GEMINI_API_KEY vào file assets/secrets.properties"));
                    mainHandler.post(callback::onStreamComplete);
                    return;
                }
                String urlStr = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:streamGenerateContent?alt=sse&key=" + apiKey;
                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setConnectTimeout(10000); // 10 seconds timeout
                conn.setReadTimeout(30000); // 30 seconds timeout
                conn.setDoOutput(true);
                JSONObject payload = new JSONObject();
                // Add system instruction
                JSONObject sysInstruction = new JSONObject();
                JSONArray sysParts = new JSONArray();
                JSONObject sysPart = new JSONObject();
                sysPart.put("text", "You are a helpful assistant for a university lab booking app. Reply concisely and clearly in Vietnamese.");
                sysParts.put(sysPart);
                sysInstruction.put("parts", sysParts);
                payload.put("system_instruction", sysInstruction);
                // Add user message history for context
                JSONArray contents = new JSONArray();
                for (ChatMessage msg : chatMessages) {
                    JSONObject part = new JSONObject();
                    part.put("text", msg.getMessage());
                    JSONObject content = new JSONObject();
                    content.put("role", msg.isUser() ? "user" : "model");
                    content.put("parts", new JSONArray().put(part));
                    contents.put(content);
                }
                // Add current user message
                JSONObject part = new JSONObject();
                part.put("text", userMessage);
                JSONObject content = new JSONObject();
                content.put("role", "user");
                content.put("parts", new JSONArray().put(part));
                contents.put(content);
                payload.put("contents", contents);
                // Add safety settings
                JSONArray safetySettings = new JSONArray();
                JSONObject safety = new JSONObject();
                safety.put("category", "HARM_CATEGORY_DANGEROUS_CONTENT");
                safety.put("threshold", "BLOCK_ONLY_HIGH");
                safetySettings.put(safety);
                payload.put("safetySettings", safetySettings);
                // Add generation config
                JSONObject genConfig = new JSONObject();
                genConfig.put("stopSequences", new JSONArray().put("Title"));
                genConfig.put("temperature", 1.0);
                genConfig.put("maxOutputTokens", 800);
                genConfig.put("topP", 0.8);
                genConfig.put("topK", 10);
                payload.put("generationConfig", genConfig);
                conn.getOutputStream().write(payload.toString().getBytes());

                // Check response code
                int responseCode = conn.getResponseCode();
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    InputStream errorStream = conn.getErrorStream();
                    String errorMessage = "Lỗi kết nối với Gemini API";
                    if (errorStream != null) {
                        BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorStream));
                        StringBuilder errorResponse = new StringBuilder();
                        String errorLine;
                        while ((errorLine = errorReader.readLine()) != null) {
                            errorResponse.append(errorLine);
                        }
                        errorReader.close();
                        try {
                            JSONObject errorJson = new JSONObject(errorResponse.toString());
                            if (errorJson.has("error")) {
                                JSONObject error = errorJson.getJSONObject("error");
                                if (error.has("message")) {
                                    errorMessage = "Lỗi: " + error.getString("message");
                                }
                            }
                        } catch (Exception e) {
                            errorMessage = "Lỗi kết nối với Gemini API (Code: " + responseCode + ")";
                        }
                    } else {
                        if (responseCode == 401) {
                            errorMessage = "Lỗi xác thực: API key không hợp lệ hoặc đã hết hạn";
                        } else if (responseCode == 400) {
                            errorMessage = "Lỗi yêu cầu: Dữ liệu gửi lên không hợp lệ";
                        } else if (responseCode == 429) {
                            errorMessage = "Quá nhiều yêu cầu: Vui lòng thử lại sau";
                        } else if (responseCode >= 500) {
                            errorMessage = "Lỗi server: Gemini API đang gặp sự cố, vui lòng thử lại sau";
                        }
                    }
                    final String finalErrorMessage = errorMessage;
                    mainHandler.post(() -> callback.onError(finalErrorMessage));
                    mainHandler.post(callback::onStreamComplete);
                    return;
                }

                InputStream is = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                String line;
                StringBuilder response = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("data: ")) {
                        String json = line.substring(6);
                        if (json.trim().isEmpty()) continue;
                        JSONObject obj = new JSONObject(json);
                        JSONArray candidates = obj.optJSONArray("candidates");
                        if (candidates != null && candidates.length() > 0) {
                            JSONObject candidate = candidates.optJSONObject(0);
                            if (candidate != null) {
                                JSONObject contentObj = candidate.optJSONObject("content");
                                if (contentObj != null) {
                                    JSONArray parts = contentObj.optJSONArray("parts");
                                    if (parts != null && parts.length() > 0) {
                                        String text = parts.optJSONObject(0).optString("text", "");
                                        response.append(text);
                                        String resp = response.toString();
                                        mainHandler.post(() -> callback.onStreamUpdate(resp));
                                    }
                                }
                            }
                        }
                    }
                }
                reader.close();
                conn.disconnect();

                // Check if we got any response
                if (response.length() == 0) {
                    mainHandler.post(() -> callback.onError("Không nhận được phản hồi từ Gemini. Vui lòng thử lại."));
                }

                // Notify completion
                mainHandler.post(callback::onStreamComplete);
            } catch (java.net.SocketTimeoutException e) {
                mainHandler.post(() -> callback.onError("Lỗi: Hết thời gian chờ kết nối. Vui lòng kiểm tra kết nối internet và thử lại."));
                mainHandler.post(callback::onStreamComplete);
            } catch (java.net.UnknownHostException e) {
                mainHandler.post(() -> callback.onError("Lỗi: Không thể kết nối đến server. Vui lòng kiểm tra kết nối internet."));
                mainHandler.post(callback::onStreamComplete);
            } catch (java.io.IOException e) {
                String errorMsg = "Lỗi kết nối: " + e.getMessage();
                if (e.getMessage() != null && e.getMessage().contains("Unable to resolve host")) {
                    errorMsg = "Lỗi: Không thể kết nối internet. Vui lòng kiểm tra kết nối mạng.";
                }
                final String finalErrorMsg = errorMsg;
                mainHandler.post(() -> callback.onError(finalErrorMsg));
                mainHandler.post(callback::onStreamComplete);
            } catch (org.json.JSONException e) {
                mainHandler.post(() -> callback.onError("Lỗi: Phản hồi từ server không hợp lệ. Vui lòng thử lại."));
                mainHandler.post(callback::onStreamComplete);
            } catch (Exception e) {
                String errorMsg = "Đã xảy ra lỗi: " + (e.getMessage() != null ? e.getMessage() : "Lỗi không xác định");
                mainHandler.post(() -> callback.onError(errorMsg));
                mainHandler.post(callback::onStreamComplete);
            }
        }).start();
    }
}
