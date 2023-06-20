package org.flagsmith;

import com.flagsmith.FlagsmithClient;
import com.flagsmith.exceptions.FlagsmithClientError;
import com.flagsmith.models.Flags;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.flagsmith.model.Book;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Main {
    private static final String CONFIG_FILE = "config.properties";
    private static final String ADD_BOOKS_FEATURE_FLAG = "add_books";
    private static final int SERVER_PORT = 8000;

    private static List<Book> books = new ArrayList<>();
    private static FlagsmithClient flagsmithClient;

    public static void main(String[] args) throws IOException {
        initializeFlagsmithClient();
        populateDummyData();

        HttpServer server = HttpServer.create(new InetSocketAddress(SERVER_PORT), 0);
        server.createContext("/books", new BooksHandler());
        server.setExecutor(null); // creates a default executor
        server.start();

        System.out.println("Server started on port " + SERVER_PORT);
    }

    private static void initializeFlagsmithClient() {
        String apiKey = readFlagsmithApiKey();
        flagsmithClient = FlagsmithClient.newBuilder()
                .setApiKey(apiKey)
                .build();
    }

    private static String readFlagsmithApiKey() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                Objects.requireNonNull(Main.class.getClassLoader().getResourceAsStream(CONFIG_FILE))))) {
            return reader.readLine().split("=")[1];
        } catch (IOException e) {
            throw new RuntimeException("Failed to read Flagsmith API key from config.properties file.", e);
        }
    }

    private static void populateDummyData() {
        books.add(new Book("1", "Harry Potter", "J.K. Rowling", "20.99"));
        books.add(new Book("2", "War and Peace", "Leo Tolstoy", "26.99"));
        books.add(new Book("3", "The Kite Runner", "Khaled Hosseini", "30.99"));
    }

    static class BooksHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String requestMethod = exchange.getRequestMethod();
            if ("GET".equals(requestMethod)) {
                handleGetBooksRequest(exchange);
            } else if ("POST".equals(requestMethod)) {
                handlePostBooksRequest(exchange);
            } else {
                sendResponse(exchange, 405, "Method not allowed");
            }
        }

        private void handleGetBooksRequest(HttpExchange exchange) throws IOException {
            String jsonResponse = new Gson().toJson(books);
            sendResponse(exchange, 200, jsonResponse);
        }

        private void handlePostBooksRequest(HttpExchange exchange) throws IOException {

            Flags flags;
            boolean allowAddBooks;
            int minPrice;

            try {
                flags = flagsmithClient.getEnvironmentFlags();
                allowAddBooks = flags.isFeatureEnabled(ADD_BOOKS_FEATURE_FLAG);
                minPrice = (int) flags.getFeatureValue(ADD_BOOKS_FEATURE_FLAG);
            } catch (FlagsmithClientError e) {
                throw new RuntimeException(e);
            }

            String resp;
            int respCode;
            if (allowAddBooks) {
                Book book = getBookFromRequestBody(exchange);
                if (Integer.parseInt(book.getPrice()) >= minPrice) {
                    books.add(book);
                    resp = "book added successfully";
                    respCode = 201;
                } else {
                    resp = "book value less than minimum price allowed";
                    respCode = 406;
                }
            } else {
                resp = "method not allowed. Please come back later";
                respCode = 403;
            }

            sendResponse(exchange, respCode, resp);
        }

        private Book getBookFromRequestBody(HttpExchange exchange) throws IOException {
            InputStreamReader isr = new InputStreamReader(exchange.getRequestBody());
            BufferedReader br = new BufferedReader(isr);
            StringBuilder requestBody = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                requestBody.append(line);
            }
            Gson gson = new Gson();
            return gson.fromJson(requestBody.toString(), Book.class);
        }

        private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
            exchange.sendResponseHeaders(statusCode, response.getBytes().length);
            OutputStream outputStream = exchange.getResponseBody();
            outputStream.write(response.getBytes());
            outputStream.close();
        }
    }
}