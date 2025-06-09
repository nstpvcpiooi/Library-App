CREATE TABLE Books
(
    bookID      VARCHAR(55) PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    author      VARCHAR(55),
    publishYear INT,
    category    VARCHAR(55),
    isbn        VARCHAR(55) UNIQUE,
    qrCode      VARCHAR(255), -- Mã QR cho sách
    coverCode   VARCHAR(255), -- Ảnh bìa của sách
    quantity    int null
);

CREATE TABLE Members
(
    memberID INT AUTO_INCREMENT PRIMARY KEY,
    userName VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    email    VARCHAR(255),
    phone    VARCHAR(55),
    otp     VARCHAR(255),
    duty     INT -- = 1 nếu là admin, 0 nếu là user
);

CREATE TABLE Requests
(
    requestID  int auto_increment
        primary key,
    memberID   int          not null,
    bookID     varchar(255) not null,
    issueDate  datetime null,
    dueDate    datetime null,
    returnDate datetime null,
    status     varchar(50) null,
    overdue    tinyint(1)   null,
    constraint requests_ibfk_1
        foreign key (memberID) references members (memberID)
            on update cascade on delete cascade,
    constraint requests_ibfk_2
        foreign key (bookID) references books (bookID)
            on update cascade on delete cascade
);

CREATE TABLE Reviews
(

    bookID          VARCHAR(55),
    memberID        INT,
    rating          INT CHECK (rating BETWEEN 1 AND 5), -- Xếp hạng từ 1 đến 5
    reviewTimestamp DATE,
    comment         TEXT

);

CREATE TABLE Recommendations
(

    memberID           INT,
    preferenceCategory VARCHAR(255)

);