# Library-App
Dương Tuấn Minh - 23021937
---

## Tài liệu đặc tả
ĐẶC TẢ ĐƯỢC ĐẶT TRONG FILE `SRS.pdf`.

---
## System Testcases
Testcases kiểm thử chức năng được đặt trong file `System Testing.pdf`

## Kiến trúc dự án

Dự án được cấu trúc theo **DAO pattern** với các thành phần chính:

- **DAO**: dùng để tương tác với database.  
- **Service**: chứa business logic của hệ thống.  
- **Model**: định nghĩa các Entity và thuộc tính.  
- **Test**: chứa mã **Unit Testing** cho các thành phần.

---

## Cấu trúc thư mục

```text
Library-App
  └─ src/
     └─ main/
        └─ java/
           └─ Library/
              └─ backend/
                 ├─ Book/
                 │  ├─ DAO/
                 │  ├─ Model/
                 │  ├─ Service/
                 │  └─ Test/
                 ├─ Member/
                 │  ├─ DAO/
                 │  ├─ Model/
                 │  ├─ Service/
                 │  └─ Test/
                 ├─ Recommendation/
                 │  ├─ DAO/
                 │  ├─ Model/
                 │  ├─ Service/
                 │  └─ Test/
                 ├─ Request/
                 │  ├─ DAO/
                 │  ├─ Model/
                 │  ├─ Service/
                 │  └─ Test/
                 └─ Review/
                    ├─ DAO/
                    ├─ Model/
                    ├─ Service/
                    └─ Test/
