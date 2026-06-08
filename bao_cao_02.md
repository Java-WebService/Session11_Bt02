Có 3 lỗi logic chính trong phương thức `updateStock`:

1. Không lưu dữ liệu vào cơ sở dữ liệu:
   - Thiếu dòng gọi `productRepository.save(product)`. Thay đổi số lượng chỉ mới cập nhật trên đối tượng Java trong bộ nhớ (RAM) chứ chưa ghi nhận vào Database.

2. Loại ngoại lệ chưa phù hợp với nghiệp vụ:
   - Khi `newStock < 0`, hệ thống ném ra `IllegalStateException`. Về mặt ngữ nghĩa, lỗi này do tham số số lượng thay đổi (`quantityChange`) không hợp lệ gây ra, nên ném `IllegalArgumentException` để đồng bộ với lỗi không tìm thấy sản phẩm.

3. Thiếu cơ chế ngăn chặn gọi `save` khi xảy ra lỗi:
   - Mặc dù code cũ quên gọi `save`, nhưng khi sửa lỗi để gọi `save`, cần đảm bảo lệnh `save` này phải nằm SAU khi kiểm tra điều kiện âm kho. Nếu đặt sai vị trí hoặc không chặn, dữ liệu lỗi vẫn có thể bị ghi xuống DB.