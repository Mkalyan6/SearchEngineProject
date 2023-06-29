package com.Package;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

@WebServlet("/Search")
public class Search extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

            // we got the keyword user, entered by user
            String keyword = request.getParameter("keyword");
            // setting up connection with database, fore getting the links and pages related to keyword;
            Connection connection = DataBaseConnector.getConnection();


            try{
                //Store the query ,user sent for history purpose
                PreparedStatement preparedStatement=connection.prepareStatement("Insert into history values(?,?);");
                preparedStatement.setString(1,keyword);
                preparedStatement.setString(2,"httpp://localhost:8080/SearchEngine/Search?keyword="+keyword);
                preparedStatement.executeUpdate();
                //getting results after running the ranking query
            ResultSet resultSet = connection.createStatement().executeQuery(" select pageTitle, PageLink, (length(lower(pageText))-length(replace(lower(pageText),'" + keyword.toLowerCase() + "','')))/length('" + keyword.toLowerCase() + "') as CountOccurence from pages order by CountOccurence desc limit 30;");
            ArrayList<SearchResult> results = new ArrayList<>();
            // adding the result data into set;
            while (resultSet.next()) {
                SearchResult searchResult = new SearchResult();
                searchResult.setTitle(resultSet.getString("pageTitle"));
                searchResult.setLink(resultSet.getString("PageLink"));
                results.add(searchResult);
            }
            for (SearchResult info : results) {
                System.out.println(info.getTitle() + "\n" + info.getLink() + "\n");
            }
            request.setAttribute("results", results);
            request.getRequestDispatcher("search.jsp").forward(request, response);
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();


        }
        catch(SQLException | ServletException sqlException){
            sqlException.printStackTrace();
        }


    }
}
