package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.ProcessBuilder.Redirect;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.MultivaluedMap;

import utility.Constants;
import utility.GlobalValue;
import model.Category;
import model.TrafficSign;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

/**
 * Servlet implementation class UserController
 */
public class UserController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UserController() {
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
		PrintWriter out = response.getWriter();
		try {
			// get action parameter
			String action = request.getParameter("action");

			if ("login".equals(action)) {
				String username = request.getParameter("txtUsername");
				String password = request.getParameter("txtPassword");
				// url login
				String url = GlobalValue.ServiceAddress
						+ Constants.LIST_CATEGORY_SERVICE;
			} else
			// load searchmanual page with all category
			if ("searchManual".equals(action)) {
				String searchKey = request.getParameter("searchKey");
				String catID = request.getParameter("catID");
				// get all category and set to attribute category for display in selectbox
				String urlGetCategory = GlobalValue.ServiceAddress + Constants.LIST_CATEGORY_SERVICE ;
				Client client = Client.create();
				WebResource webResource = client.resource(urlGetCategory);
				ClientResponse clientResponse = webResource.accept(
						"application/json").get(ClientResponse.class);
				// handle error (send redirect to error page)
				if (response.getStatus() != 200) {
					response.sendRedirect(Constants.ERROR_PAGE);
				}

				String output = clientResponse.getEntity(String.class);
				// Arraylist Category to contain all category
				ArrayList<Category> category = new ArrayList<Category>();
				// parse output to List category using Gson
				Gson gson = new Gson();
				Type type = new TypeToken<ArrayList<Category>>(){}.getType();
				category = gson.fromJson(output, type);
				request.setAttribute("category", category);
				// excute search manual if search key or categoryID is not null
				if(searchKey != null && catID != null){
					String urlSearchManual = GlobalValue.ServiceAddress + Constants.SEARCH_MANUAL_SERVICE + "?";
					urlSearchManual = urlSearchManual + "name=" + URLEncoder.encode(searchKey, "UTF-8");
					urlSearchManual = urlSearchManual + "&cateID=" + catID;
					// connect and receive json string from web service
					WebResource webRsource = client.resource(urlSearchManual);
					clientResponse = webRsource.accept(
							"application/json").get(ClientResponse.class);
					// handle error (not implement yet)
					if (response.getStatus() != 200) {
						response.sendRedirect(Constants.ERROR_PAGE);
					}
					String searchString = clientResponse.getEntity(String.class);
					ArrayList<TrafficSign> listTraffic = new ArrayList<TrafficSign>();
					// parse output to list trafficSign using Gson
					Type typeSearch = new TypeToken<ArrayList<TrafficSign>>() {
					}.getType();
					listTraffic = gson.fromJson(searchString, typeSearch);
					request.setAttribute("listTraffic", listTraffic);
				}
				// request to searchManual.jsp
				
				RequestDispatcher rd = request
						.getRequestDispatcher("User/SearchManual.jsp");
				rd.forward(request, response);

			} else
			// search traffic by name and return to searchManual page
			if ("searchTraffic".equals(action)) {
				String searchKey = request.getParameter("searchKey");
				// searchKey = searchKey.replace(" ", "%20");
				// url get traffic by categoryID
				String url = "http://bienbaogiaothong.tk/rest/Service/SearchByName?name=";
				url += URLEncoder.encode(searchKey, "UTF-8");
				// connect and receive json string from web service
				Client client = Client.create();
				WebResource webRsource = client.resource(url);
				ClientResponse clientResponse = webRsource.accept(
						"application/json").get(ClientResponse.class);
				// handle error (not implement yet)
				if (response.getStatus() != 200) {
					throw new RuntimeException("Failed : HTTP error code : "
							+ response.getStatus());
				}
				String output = clientResponse.getEntity(String.class);
				ArrayList<TrafficSign> listTraffic = new ArrayList<TrafficSign>();
				// parse output to list trafficSign using Gson
				Gson gson = new Gson();
				Type type = new TypeToken<ArrayList<TrafficSign>>() {
				}.getType();
				listTraffic = gson.fromJson(output, type);
				// request to searchManual.jsp
				request.setAttribute("listTraffic", listTraffic);
				RequestDispatcher rd = request
						.getRequestDispatcher("User/SearchManual.jsp");
				rd.forward(request, response);

			} else
			// if action is show traffic details
			if ("viewDetail".equals(action)) {
				String trafficID = request.getParameter("trafficID");
				// url get traffic by categoryID
				String url = "http://bienbaogiaothong.tk/rest/Service/ViewDetail?id=";
				url += trafficID;
				// connect and receive json string from web service
				Client client = Client.create();
				WebResource webRsource = client.resource(url);
				ClientResponse clientResponse = webRsource.accept(
						"application/json").get(ClientResponse.class);
				// handle error (not implement yet)
				if (response.getStatus() != 200) {
					throw new RuntimeException("Failed : HTTP error code : "
							+ response.getStatus());
				}
				String output = clientResponse.getEntity(String.class);
				TrafficSign trafficDetail = new TrafficSign();
				// parse output to list trafficSign using Gson
				Gson gson = new Gson();
				Type type = new TypeToken<TrafficSign>() {
				}.getType();
				trafficDetail = gson.fromJson(output, type);
				// request to searchManual.jsp
				request.setAttribute("trafficDetail", trafficDetail);
				RequestDispatcher rd = request
						.getRequestDispatcher("User/TrafficDetail.jsp");
				rd.forward(request, response);
			}

		} catch (Exception e) {
			e.printStackTrace();
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
		// TODO Auto-generated method stub
		processRequest(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		processRequest(request, response);

	}

}
