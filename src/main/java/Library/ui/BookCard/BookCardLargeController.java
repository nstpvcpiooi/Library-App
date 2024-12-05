package Library.ui.BookCard;

import Library.backend.Request.DAO.RequestDAO;
import Library.backend.Request.DAO.RequestDAOImpl;
import Library.backend.Request.Model.Request;
import Library.backend.Session.SessionManager;
import Library.backend.bookModel.Book;
import javafx.scene.image.Image;

import static Library.ui.MainController.DEFAULT_COVER;

/**
 * Controller cho một card sách lớn (hiển thị ảnh bìa, tiêu đề, tác giả).
 */
public class BookCardLargeController extends BookCardController {

    @Override
    public void setData(Book book) {
        // 1. LẤY ẢNH BÌA SÁCH


        // 2. LẤY TIÊU ĐỀ
        title.setText(book.getTitle());

        if (SessionManager.getInstance().getLoggedInMember().getDuty()==0)
            if (RequestDAOImpl.getInstance().getRequestByMemberIDAndBookID(SessionManager.getInstance().getLoggedInMember().getMemberID(), book.getBookID()) != null) {
                if (RequestDAOImpl.getInstance().
                        getRequestByMemberIDAndBookID
                                (SessionManager.getInstance().getLoggedInMember().getMemberID(),
                                        book.getBookID()).isOverdue()) {
                    System.out.println("Overdue");
                    OverdueTag.setText("Quá hạn");
                } else {
                    OverdueTag.setVisible(false);


                }
            }
        // 3. LẤY TÊN TÁC GIẢ
        author.setText(book.getAuthor());

        if (SessionManager.getInstance().getLoggedInMember().getDuty()==1)   {
            OverdueTag.setVisible(false);

            quantity.setText("Số lượng: " + book.getQuantity());
            quantity.setVisible(true);
        } else {
            quantity.setVisible(false);
        }
        // if request exists and returnDate is null, show due date
        // (because it is not returned yet so we can show due date)
        // add highlight to the card if overdue
        if (RequestDAOImpl.getInstance().getRequestByMemberIDAndBookID(SessionManager.getInstance().getLoggedInMember().getMemberID(), book.getBookID()) != null) {
            Request request = RequestDAOImpl.getInstance().
                    getRequestByMemberIDAndBookID(SessionManager.
                            getInstance().getLoggedInMember().
                            getMemberID(), book.getBookID());
            if (request.getReturnDate() == null)
            {

                System.out.println(request.getDueDate());
            }




        }


    }
}

