# Cấu hình API Keys

## File secrets.properties

File này chứa các API keys cần thiết cho ứng dụng.

### Cấu hình Gemini API Key

1. Truy cập: https://makersuite.google.com/app/apikey
2. Đăng nhập bằng tài khoản Google
3. Tạo API key mới hoặc sử dụng API key hiện có
4. Mở file `secrets.properties` trong thư mục này
5. Thay `YOUR_GEMINI_API_KEY` bằng API key thực tế của bạn:

```
GEMINI_API_KEY=your_actual_api_key_here
```

### Lưu ý

- **KHÔNG** commit file `secrets.properties` vào Git repository
- File này đã được thêm vào `.gitignore`
- Mỗi developer cần tạo API key riêng của mình
- API key có giới hạn số lượng request, sử dụng cẩn thận

### Kiểm tra cấu hình

Sau khi cấu hình, khởi động lại ứng dụng và thử chức năng chat trợ lý ảo.
Nếu có lỗi, kiểm tra:
- API key đã được thêm đúng chưa
- API key còn hiệu lực không
- Có kết nối internet không



