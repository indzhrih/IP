package com.example.library.repository;

import com.example.library.model.AppUser;
import com.example.library.model.BorrowedBook;
import com.example.library.model.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class UserXmlRepository {

    private final Path usersXmlPath;
    private final Path usersXsdPath;

    public UserXmlRepository(@Value("${users.xml.path}") String usersPath,
                             @Value("${users.xsd.path}") String usersXsdPath) {
        this.usersXmlPath = Path.of(usersPath);
        this.usersXsdPath = Path.of(usersXsdPath);
    }

    public synchronized List<AppUser> loadUsers() {
        try {
            ensureUsersFileExists();
            validateUsersXml();
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            try (InputStream in = Files.newInputStream(usersXmlPath)) {
                Document document = builder.parse(in);
                return parseUsers(document);
            }
        } catch (Exception exception) {
            throw new RuntimeException("Ошибка чтения users.xml: " + exception.getMessage(), exception);
        }
    }

    public synchronized void saveUsers(List<AppUser> users) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.newDocument();

            Element root = document.createElement("users");
            root.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
            root.setAttribute("xsi:noNamespaceSchemaLocation", usersXsdPath.getFileName().toString());
            document.appendChild(root);

            for (AppUser user : users) {
                Element userElement = document.createElement("user");
                userElement.setAttribute("username", user.getUsername());
                userElement.setAttribute("password", user.getPassword());
                userElement.setAttribute("role", user.getRole().name());

                Element borrowedRoot = document.createElement("borrowedBooks");
                for (BorrowedBook borrowed : user.getBorrowedBooks()) {
                    Element borrowedElement = document.createElement("borrowedBook");
                    borrowedElement.setAttribute("bookId", Integer.toString(borrowed.getBookId()));
                    if (borrowed.getIssueDate() != null) {
                        borrowedElement.setAttribute("issueDate", borrowed.getIssueDate().toString());
                    }
                    borrowedRoot.appendChild(borrowedElement);
                }
                userElement.appendChild(borrowedRoot);

                root.appendChild(userElement);
            }

            if (!Files.exists(usersXmlPath.getParent())) {
                Files.createDirectories(usersXmlPath.getParent());
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            try (var out = Files.newOutputStream(usersXmlPath)) {
                transformer.transform(new DOMSource(document), new StreamResult(out));
            }

            validateUsersXml();
        } catch (ParserConfigurationException | TransformerException | IOException | SAXException exception) {
            throw new RuntimeException("Ошибка записи users.xml: " + exception.getMessage(), exception);
        }
    }

    private void ensureUsersFileExists() throws ParserConfigurationException, TransformerException, IOException {
        if (Files.exists(usersXmlPath)) {
            return;
        }

        if (!Files.exists(usersXmlPath.getParent())) {
            Files.createDirectories(usersXmlPath.getParent());
        }

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.newDocument();

        Element root = document.createElement("users");
        root.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        root.setAttribute("xsi:noNamespaceSchemaLocation", usersXsdPath.getFileName().toString());
        document.appendChild(root);

        Element librarian = document.createElement("user");
        librarian.setAttribute("username", "librarian");
        librarian.setAttribute("password", "librarian");
        librarian.setAttribute("role", "LIBRARIAN");
        librarian.appendChild(document.createElement("borrowedBooks"));
        root.appendChild(librarian);

        Element reader = document.createElement("user");
        reader.setAttribute("username", "reader1");
        reader.setAttribute("password", "reader1");
        reader.setAttribute("role", "READER");
        reader.appendChild(document.createElement("borrowedBooks"));
        root.appendChild(reader);

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

        try (var out = Files.newOutputStream(usersXmlPath)) {
            transformer.transform(new DOMSource(document), new StreamResult(out));
        }
    }

    private void validateUsersXml() throws IOException, SAXException {
        if (!Files.exists(usersXsdPath)) {
            throw new IOException("XSD-схема для пользователей не найдена: " + usersXsdPath);
        }
        if (!Files.exists(usersXmlPath)) {
            throw new IOException("XML-файл пользователей не найден: " + usersXmlPath);
        }

        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = schemaFactory.newSchema(usersXsdPath.toFile());
        Validator validator = schema.newValidator();

        try (InputStream in = Files.newInputStream(usersXmlPath)) {
            validator.validate(new StreamSource(in));
        }
    }

    private List<AppUser> parseUsers(Document document) {
        List<AppUser> result = new ArrayList<>();
        NodeList userNodes = document.getElementsByTagName("user");
        for (int i = 0; i < userNodes.getLength(); i++) {
            Element element = (Element) userNodes.item(i);
            String username = element.getAttribute("username");
            String password = element.getAttribute("password");
            String roleText = element.getAttribute("role");
            Role role = Role.valueOf(roleText);

            List<BorrowedBook> borrowedBooks = new ArrayList<>();
            NodeList borrowedRoots = element.getElementsByTagName("borrowedBooks");
            if (borrowedRoots.getLength() > 0) {
                Element borrowedRoot = (Element) borrowedRoots.item(0);
                NodeList borrowedNodes = borrowedRoot.getElementsByTagName("borrowedBook");
                for (int j = 0; j < borrowedNodes.getLength(); j++) {
                    Element borrowedElement = (Element) borrowedNodes.item(j);
                    int bookId = Integer.parseInt(borrowedElement.getAttribute("bookId"));
                    String dateText = borrowedElement.getAttribute("issueDate");
                    LocalDate date = dateText == null || dateText.isBlank()
                            ? null
                            : LocalDate.parse(dateText);
                    borrowedBooks.add(new BorrowedBook(bookId, date));
                }
            }

            AppUser user = new AppUser(username, password, role, borrowedBooks);
            result.add(user);
        }
        return result;
    }
}
