# Flagsmith: Java Feature Flag Demo

This is a Java server application that demonstrates how to use feature flags with the Flagsmith Java SDK. It allows you to control the behavior of the application dynamically without redeployment.

## Introduction to Feature Flags

Feature flags, also known as feature toggles, are a powerful technique used to modify the functionality of an application without making code changes or redeployments. They provide the ability to turn features on/off or change their behavior based on configurable flags. Feature flags are commonly used for phased rollouts, A/B testing, segmentation, and gradual feature releases.

## Project Structure

The project follows the structure below:


- `Main.java`: The main class that sets up the HTTP server, handles requests, and integrates with the Flagsmith Java SDK.
- `Book.java`: A simple model class representing a book.
- `config.properties`: Configuration file containing the Flagsmith API key.

## Getting Started

To run the project locally, follow these steps:

1. Clone the repository.
2. Open the project in your preferred Java IDE.
3. Set your Flagsmith API key in the `config.properties` file.
4. Build the project using Maven.
5. Run the `Main` class.

## Usage

Once the server is running, you can send GET and POST requests to `http://localhost:8000/books`.

- **GET /books**: Retrieves a list of books in the server's records.
- **POST /books**: Adds a new book to the server's records.

The behavior of the POST endpoint can be controlled using the Flagsmith feature flag `add_books`. When the feature flag is enabled, new books can be added. When the feature flag is disabled, adding books is not allowed.

## Flagsmith Integration

This project integrates with the Flagsmith Java SDK to control the behavior using feature flags. The Flagsmith SDK allows you to query feature flag values and make decisions based on them. The SDK is initialized with your Flagsmith API key in the `Main.java` class.

To use feature flags, create an account on [Flagsmith](https://flagsmith.com/) and obtain an API key. Set this API key in the `config.properties` file.

## Dependencies

This project relies on the following dependencies:

- [Gson](https://github.com/google/gson): A library for JSON serialization/deserialization.
- [Flagsmith Java SDK](https://github.com/flagsmith/flagsmith-java-client): The Java SDK for interacting with Flagsmith.

These dependencies are managed through Maven and will be automatically downloaded when building the project.

## Contributing

Contributions are welcome! If you find any issues or have suggestions for improvements, please open an issue or submit a pull request.

## License

This project is licensed under the [MIT License](LICENSE).
