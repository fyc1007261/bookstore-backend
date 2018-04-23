package bookinfo;

import database.BookEntity;
import database.CustomerEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import database.OrderformEntity;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/purchase/change_amount")
public class ChangeOrder extends HttpServlet {

    private static final SessionFactory sessionFactory;

    // 加载配置文件，并创建表
    static {
        Configuration configuration = new Configuration();
        configuration.configure();
        sessionFactory = configuration.buildSessionFactory();
    }

    public static Session getSession() {
        return sessionFactory.openSession();
    }

    private String message;

    @Override
    public void init() throws ServletException {
        message = "No info received";
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // set up hibernate
        final Session session = getSession();
        Transaction transaction = session.beginTransaction();

        String usn = (String)req.getSession().getAttribute("user");
        if (usn==null){
            message = "Not signed in";
        }

        try{
            resp.setContentType("text/html");
            PrintWriter out = resp.getWriter();

            String order_id =req.getParameter("order_id");
            int new_num = Integer.parseInt(req.getParameter("new_amount"));
            if(new_num<=0){
                OrderformEntity order = session.get(OrderformEntity.class, order_id);
                session.delete(order);
                transaction.commit();
                message = "Succeed";
            }
            else{
                OrderformEntity order = session.get(OrderformEntity.class, order_id);
                order.setAmount(new_num);
                transaction.commit();
                message = "Succeed";
            }

            out.print(message);
        }
        catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        }
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }


    @Override
    public void destroy() {
        super.destroy();
    }
}
