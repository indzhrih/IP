package org.example.xmlparser.repository;

import org.example.xmlparser.model.Book;
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
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class LibraryXmlRepository {

    public List<Book> loadBooks(Path xmlPath, Path xsdPath)
            throws IOException, SAXException, ParserConfigurationException {

        validateXml(xmlPath, xsdPath);

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        try (InputStream in = Files.newInputStream(xmlPath)) {
            Document document = builder.parse(in);
            return parseBooks(document);
        }
    }

    public void saveBooks(Path xmlPath, Path xsdPath, List<Book> books)
            throws ParserConfigurationException, TransformerException, IOException, SAXException {

        Document document = buildDocumentFromBooks(books, xsdPath);

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

        try (var out = Files.newOutputStream(xmlPath)) {
            transformer.transform(new DOMSource(document), new StreamResult(out));
        }

        validateXml(xmlPath, xsdPath);
    }

    public void validateXml(Path xmlPath, Path xsdPath) throws IOException, SAXException {
        if (!Files.exists(xmlPath)) {
            throw new IOException("XML-файл не найден: " + xmlPath);
        }
        if (!Files.exists(xsdPath)) {
            throw new IOException("XSD-схема не найдена: " + xsdPath);
        }

        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema;
        try {
            schema = schemaFactory.newSchema(xsdPath.toFile());
        } catch (SAXException exception) {
            throw new SAXException("Ошибка в XSD-схеме: " + exception.getMessage(), exception);
        }

        Validator validator = schema.newValidator();
        try (InputStream in = Files.newInputStream(xmlPath)) {
            validator.validate(new StreamSource(in));
        } catch (SAXException exception) {
            throw new SAXException("XML не соответствует схеме: " + exception.getMessage(), exception);
        }
    }

    public List<Book> searchBooksUsingXPath(List<Book> books,
                                            String authorContains,
                                            Integer yearEquals,
                                            String categoryContains) throws ParserConfigurationException {
        Document document = buildDocumentFromBooks(books, null);

        StringBuilder expressionBuilder = new StringBuilder("/library/book");

        if (authorContains != null && !authorContains.isBlank()) {
            String literal = buildXPathLiteral(authorContains.trim());
            expressionBuilder.append("[contains(author, ").append(literal).append(")]");
        }

        if (yearEquals != null) {
            expressionBuilder.append("[year = ").append(yearEquals).append("]");
        }

        if (categoryContains != null && !categoryContains.isBlank()) {
            String literal = buildXPathLiteral(categoryContains.trim());
            expressionBuilder.append("[contains(category, ").append(literal).append(")]");
        }

        String expression = expressionBuilder.toString();

        XPathFactory xPathFactory = XPathFactory.newInstance();
        XPath xPath = xPathFactory.newXPath();

        List<Book> result = new ArrayList<>();
        try {
            XPathExpression xPathExpression = xPath.compile(expression);
            NodeList nodes = (NodeList) xPathExpression.evaluate(document, XPathConstants.NODESET);
            for (int i = 0; i < nodes.getLength(); i++) {
                Element element = (Element) nodes.item(i);
                Book book = createBookFromElement(element);
                result.add(book);
            }
        } catch (Exception exception) {
            throw new ParserConfigurationException("Ошибка при выполнении XPath-запроса: " + exception.getMessage());
        }

        return result;
    }

    private Document buildDocumentFromBooks(List<Book> books, Path xsdPath) throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.newDocument();

        Element root = document.createElement("library");
        if (xsdPath != null) {
            root.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
            root.setAttribute("xsi:noNamespaceSchemaLocation", xsdPath.getFileName().toString());
        }
        document.appendChild(root);

        for (Book book : books) {
            Element bookElement = document.createElement("book");
            bookElement.setAttribute("id", Integer.toString(book.getId()));
            bookElement.setAttribute("totalCopies", Integer.toString(book.getTotalCopies()));
            bookElement.setAttribute("availableCopies", Integer.toString(book.getAvailableCopies()));

            Element titleElement = document.createElement("title");
            titleElement.setTextContent(book.getTitle());
            bookElement.appendChild(titleElement);

            Element authorElement = document.createElement("author");
            authorElement.setTextContent(book.getAuthor());
            bookElement.appendChild(authorElement);

            Element yearElement = document.createElement("year");
            yearElement.setTextContent(Integer.toString(book.getYear()));
            bookElement.appendChild(yearElement);

            Element priceElement = document.createElement("price");
            priceElement.setTextContent(Double.toString(book.getPrice()));
            bookElement.appendChild(priceElement);

            Element categoryElement = document.createElement("category");
            categoryElement.setTextContent(book.getCategory());
            bookElement.appendChild(categoryElement);

            root.appendChild(bookElement);
        }

        return document;
    }

    private List<Book> parseBooks(Document document) {
        List<Book> books = new ArrayList<>();
        NodeList bookNodes = document.getElementsByTagName("book");
        for (int i = 0; i < bookNodes.getLength(); i++) {
            Element element = (Element) bookNodes.item(i);
            Book book = createBookFromElement(element);
            books.add(book);
        }
        return books;
    }

    private Book createBookFromElement(Element element) {
        int id = Integer.parseInt(element.getAttribute("id"));
        int totalCopies = Integer.parseInt(element.getAttribute("totalCopies"));
        int availableCopies = Integer.parseInt(element.getAttribute("availableCopies"));

        String title = getChildText(element, "title");
        String author = getChildText(element, "author");
        int year = Integer.parseInt(getChildText(element, "year"));

        String priceText = getChildText(element, "price").trim().replace(',', '.');
        double price = Double.parseDouble(priceText);

        String category = getChildText(element, "category");

        return new Book(id, title, author, year, price, category, totalCopies, availableCopies);
    }

    private String getChildText(Element parent, String tagName) {
        NodeList list = parent.getElementsByTagName(tagName);
        if (list.getLength() == 0) {
            return "";
        }
        return list.item(0).getTextContent().trim();
    }

    private String buildXPathLiteral(String value) {
        if (!value.contains("'")) {
            return "'" + value + "'";
        } else if (!value.contains("\"")) {
            return "\"" + value + "\"";
        } else {
            StringBuilder builder = new StringBuilder("concat(");
            boolean first = true;
            int start = 0;
            for (int i = 0; i < value.length(); i++) {
                char c = value.charAt(i);
                if (c == '\'') {
                    if (start < i) {
                        if (!first) {
                            builder.append(", ");
                        }
                        builder.append("'").append(value, start, i).append("'");
                        first = false;
                    }
                    if (!first) {
                        builder.append(", ");
                    }
                    builder.append("\"'\"");
                    first = false;
                    start = i + 1;
                }
            }
            if (start < value.length()) {
                if (!first) {
                    builder.append(", ");
                }
                builder.append("'").append(value.substring(start)).append("'");
            }
            builder.append(")");
            return builder.toString();
        }
    }
}
