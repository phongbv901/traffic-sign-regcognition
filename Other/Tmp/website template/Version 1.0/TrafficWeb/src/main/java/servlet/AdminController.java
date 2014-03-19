package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.MultivaluedMap;

import model.Report;
import model.TrafficSign;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

/**
 * Servlet implementation class AdminController
 */
public class AdminController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public AdminController() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
	 * methods.
	 * 
	 * @param request
	 *            servlet request
	 * @param response
	 *            servlet response
	 * @throws ServletException
	 *             if a servlet-specific error occurs
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	protected void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
		request.setCharacterEncoding("utf-8");
		PrintWriter out = response.getWriter();
		try {
			// get action parameter
			String action = request.getParameter("action");
			if (("login").equals(action)) {
				String userid = request.getParameter("txtUser");
				String password = request.getParameter("txtPassword");
				String url = "http://localhost:8090/Traffic/rest/Service/Login";
				Client client = Client.create();
				client.setFollowRedirects(true);
				WebResource resource = client.resource(url);
				MultivaluedMap<String, String> params = new MultivaluedMapImpl();
				params.add("userID", userid);
				params.add("password", password);
				String res = resource.path("login").queryParams(params)
						.get(String.class);
				boolean result = Boolean.parseBoolean(res);
				String path = "Admin/Login.jsp";
				if (result) {
					HttpSession session = request.getSession();
					session.setAttribute("USER", userid);
					path = "Admin/ReportPage.jsp";
				}
				response.sendRedirect(path);
			} else if (("listReport").equals(action)) {
				String url = "http://localhost:8090/Traffic/rest/Service/ListReport";
				Client client = Client.create();
				WebResource webResource = client.resource(url);
				ClientResponse clientResponse = webResource.accept(
						"application/json").get(ClientResponse.class);
				if (response.getStatus() != 200) {
					throw new RuntimeException("Failed : HTTP error code : "
							+ response.getStatus());
				}

				String output = clientResponse.getEntity(String.class);
				ArrayList<Report> listreport = new ArrayList<Report>();
				//Parse out put to gson
				Gson gson = new Gson();
				Type type = new TypeToken<ArrayList<Report>>() {
				}.getType();
				listreport = gson.fromJson(output, type);				
				request.setAttribute("listReport", listreport);
				// request to ReportPage.jsp
				request.setAttribute("report", output);
				RequestDispatcher rd = request
						.getRequestDispatcher("Admin/ReportPage.jsp");
				rd.forward(request, response);

			} else if (("listAccount").equals(action)) {
				String url = "http://localhost:8090/Traffic/rest/Service/ListAccount";
				Client client = Client.create();
				WebResource webResource = client.resource(url);
				ClientResponse clientResponse = webResource.accept(
						"application/json").get(ClientResponse.class);
				if (response.getStatus() != 200) {
					throw new RuntimeException("Failed : HTTP error code : "
							+ response.getStatus());
				}

				String output = clientResponse.getEntity(String.class);
				// request to searchManual.jsp
				request.setAttribute("account", output);
				RequestDispatcher rd = request
						.getRequestDispatcher("Admin/AccountPage.jsp");
				rd.forward(request, response);
			}else if (("viewReportDetail").equals(action)) {
				String url = "http://localhost:8090/Traffic/rest/Service/GetReportDetail";
				Client client = Client.create();
				WebResource webResource = client.resource(url);
				ClientResponse clientResponse = webResource.accept(
						"application/json").get(ClientResponse.class);
				if (response.getStatus() != 200) {
					throw new RuntimeException("Failed : HTTP error code : "
							+ response.getStatus());
				}

				String output = clientResponse.getEntity(String.class);
				// request to searchManual.jsp
				request.setAttribute("detail", output);
				RequestDispatcher rd = request
						.getRequestDispatcher("Admin/DetailReport.jsp");
				rd.forward(request, response);
			}
			else if (("deleteReport").equals(action)) {
				String reportID = request.getParameter("ReportID");
				String url = "http://localhost:8090/Traffic/rest/Service/DeleteReport?reportID=";
				url += reportID;
				Client client = Client.create();
				WebResource webResource = client.resource(url);
				ClientResponse clientResponse = webResource.accept(
						"application/json").get(ClientResponse.class);
				if (response.getStatus() != 200) {
					throw new RuntimeException("Failed : HTTP error code : "
							+ response.getStatus());
				}

				String output = clientResponse.getEntity(String.class);
				ArrayList<Report> listreport = new ArrayList<Report>();
				//Parse out put to gson
				Gson gson = new Gson();
				Type type = new TypeToken<ArrayList<Report>>() {
				}.getType();
				listreport = gson.fromJson(output, type);				
				request.setAttribute("listReport", listreport);
				// request to ReportPage.jsp
				request.setAttribute("report", output);
				RequestDispatcher rd = request
						.getRequestDispatcher("Admin/ReportPage.jsp");
				rd.forward(request, response);
			}			
		} finally {
			out.close();
		}

	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		// TODO Auto-generated method stub
		processRequest(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		// TODO Auto-generated method stub
		processRequest(request, response);

	}

}
