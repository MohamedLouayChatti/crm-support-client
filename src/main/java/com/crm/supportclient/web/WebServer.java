package com.crm.supportclient.web;

import com.crm.supportclient.external.Client;
import com.crm.supportclient.external.SupportClient;
import com.crm.supportclient.model.ArticleBaseConnaissance;
import com.crm.supportclient.model.Ticket;
import com.crm.supportclient.observer.ClientNotification;
import com.crm.supportclient.observer.SupportNotification;
import com.crm.supportclient.service.PortailClient;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Simple Web Server for testing the CRM Support Client module.
 * Provides a REST API and serves static HTML files.
 */
public class WebServer {

    private final HttpServer server;
    private final PortailClient portail;
    private final Map<Integer, Ticket> tickets;
    private final Map<Integer, Client> clients;
    private final Map<String, SupportClient> supportAgents;
    private final List<String> eventLog;

    public WebServer(int port) throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        portail = new PortailClient();
        tickets = new HashMap<>();
        clients = new HashMap<>();
        supportAgents = new HashMap<>();
        eventLog = new CopyOnWriteArrayList<>();

        initializeSampleData();
        setupRoutes();

        server.setExecutor(null);
    }

    private void initializeSampleData() {
        // Create sample clients
        Client client1 = new Client(1, "Jean Dupont", "jean.dupont@email.com");
        Client client2 = new Client(2, "Marie Martin", "marie.martin@email.com");
        clients.put(1, client1);
        clients.put(2, client2);

        // Create sample support agents
        SupportClient agent1 = new SupportClient("Pierre Support", "pierre@crm.com", "Technique");
        SupportClient agent2 = new SupportClient("Sophie Agent", "sophie@crm.com", "Commercial");
        supportAgents.put("pierre", agent1);
        supportAgents.put("sophie", agent2);

        // Create sample knowledge base articles
        portail.ajouterArticle(new ArticleBaseConnaissance(
            "Comment réinitialiser mon mot de passe?",
            "Pour réinitialiser votre mot de passe: 1. Cliquez sur 'Mot de passe oublié' 2. Entrez votre email 3. Suivez les instructions reçues par email"
        ));
        portail.ajouterArticle(new ArticleBaseConnaissance(
            "Guide de démarrage rapide",
            "Bienvenue! Ce guide vous aidera à prendre en main notre solution CRM en quelques étapes simples."
        ));
        portail.ajouterArticle(new ArticleBaseConnaissance(
            "FAQ - Questions fréquentes",
            "Retrouvez ici les réponses aux questions les plus fréquemment posées par nos utilisateurs."
        ));

        logEvent("[START] Systeme initialise avec donnees de demonstration");
    }

    private void setupRoutes() {
        server.createContext("/", new StaticFileHandler());
        server.createContext("/api/tickets", new TicketHandler());
        server.createContext("/api/tickets/action", new TicketActionHandler());
        server.createContext("/api/articles", new ArticleHandler());
        server.createContext("/api/events", new EventHandler());
        server.createContext("/api/clients", new ClientHandler());
        server.createContext("/api/agents", new AgentHandler());
    }

    public void start() {
        server.start();
        System.out.println("\n" + "=".repeat(60));
        System.out.println("[SERVER] Web Server started at http://localhost:" + server.getAddress().getPort());
        System.out.println("   Open this URL in your browser to test the application!");
        System.out.println("=".repeat(60) + "\n");
    }

    private void logEvent(String message) {
        String timestamp = java.time.LocalDateTime.now()
            .format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
        eventLog.add(0, "[" + timestamp + "] " + message);
        if (eventLog.size() > 50) {
            eventLog.remove(eventLog.size() - 1);
        }
    }

    // ==================== HTTP Handlers ====================

    class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if (path.equals("/") || path.equals("/index.html")) {
                sendHtmlPage(exchange);
            } else {
                send404(exchange);
            }
        }

        private void sendHtmlPage(HttpExchange exchange) throws IOException {
            String html = getIndexHtml();
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }

        private void send404(HttpExchange exchange) throws IOException {
            String response = "404 Not Found";
            exchange.sendResponseHeaders(404, response.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }

    class TicketHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            
            if ("GET".equals(method)) {
                handleGetTickets(exchange);
            } else if ("POST".equals(method)) {
                handleCreateTicket(exchange);
            } else {
                exchange.sendResponseHeaders(405, -1);
            }
        }

        private void handleGetTickets(HttpExchange exchange) throws IOException {
            StringBuilder json = new StringBuilder("[");
            boolean first = true;
            for (Ticket ticket : tickets.values()) {
                if (!first) json.append(",");
                json.append(ticketToJson(ticket));
                first = false;
            }
            json.append("]");
            sendJsonResponse(exchange, json.toString());
        }

        private void handleCreateTicket(HttpExchange exchange) throws IOException {
            Map<String, String> params = parseFormData(exchange);
            
            String titre = params.getOrDefault("titre", "Sans titre");
            String description = params.getOrDefault("description", "");
            String priorite = params.getOrDefault("priorite", "Moyenne");
            int clientId = Integer.parseInt(params.getOrDefault("clientId", "1"));

            Ticket ticket = new Ticket(titre, description, priorite);
            
            // Attach observers
            Client client = clients.get(clientId);
            if (client != null) {
                ClientNotification clientNotif = new ClientNotification(client.getId(), client.getEmail());
                ticket.attach(clientNotif);
            }
            
            // Attach support notification (using a generic support agent for initial notifications)
            SupportClient defaultAgent = supportAgents.values().iterator().next();
            SupportNotification supportNotif = new SupportNotification(
                defaultAgent.getId(), 
                defaultAgent.getNom(), 
                defaultAgent.getEmail()
            );
            ticket.attach(supportNotif);
            
            ticket.creer();
            tickets.put(ticket.getId(), ticket);
            portail.ajouterTicket(clientId, ticket);
            
            logEvent("[TICKET] Ticket #" + ticket.getId() + " cree: " + titre + " (Priorite: " + priorite + ")");
            
            sendJsonResponse(exchange, ticketToJson(ticket));
        }
    }

    class TicketActionHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

            Map<String, String> params = parseFormData(exchange);
            int ticketId = Integer.parseInt(params.getOrDefault("ticketId", "0"));
            String action = params.getOrDefault("action", "");
            
            Ticket ticket = tickets.get(ticketId);
            if (ticket == null) {
                sendJsonResponse(exchange, "{\"error\": \"Ticket not found\"}");
                return;
            }

            String previousState = ticket.getState().getNomEtat();
            
            switch (action) {
                case "assigner" -> {
                    String agentId = params.getOrDefault("agentId", "");
                    SupportClient agent = supportAgents.get(agentId);
                    if (agent != null) {
                        ticket.assigner(agent.getId());
                        logEvent("[ASSIGN] Ticket #" + ticketId + " assigne a " + agent.getNom() + " (" + previousState + " -> " + ticket.getState().getNomEtat() + ")");
                    }
                }
                case "resoudre" -> {
                    String solution = params.getOrDefault("solution", "Solution non specifiee");
                    ticket.resoudre(solution);
                    logEvent("[RESOLVED] Ticket #" + ticketId + " resolu (" + previousState + " -> " + ticket.getState().getNomEtat() + ")");
                }
                case "fermer" -> {
                    ticket.fermer();
                    logEvent("[CLOSED] Ticket #" + ticketId + " ferme (" + previousState + " -> " + ticket.getState().getNomEtat() + ")");
                }
                default -> {
                    sendJsonResponse(exchange, "{\"error\": \"Unknown action\"}");
                    return;
                }
            }

            sendJsonResponse(exchange, ticketToJson(ticket));
        }
    }

    class ArticleHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

            var articles = portail.consulterBaseConnaissance();
            StringBuilder json = new StringBuilder("[");
            boolean first = true;
            for (var article : articles) {
                if (!first) json.append(",");
                json.append("{")
                    .append("\"titre\":\"").append(escapeJson(article.getTitre())).append("\",")
                    .append("\"contenu\":\"").append(escapeJson(article.getContenu())).append("\"")
                    .append("}");
                first = false;
            }
            json.append("]");
            sendJsonResponse(exchange, json.toString());
        }
    }

    class EventHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

            StringBuilder json = new StringBuilder("[");
            boolean first = true;
            for (String event : eventLog) {
                if (!first) json.append(",");
                json.append("\"").append(escapeJson(event)).append("\"");
                first = false;
            }
            json.append("]");
            sendJsonResponse(exchange, json.toString());
        }
    }

    class ClientHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            StringBuilder json = new StringBuilder("[");
            boolean first = true;
            for (Client client : clients.values()) {
                if (!first) json.append(",");
                json.append("{")
                    .append("\"id\":").append(client.getId()).append(",")
                    .append("\"nom\":\"").append(escapeJson(client.getNom())).append("\",")
                    .append("\"email\":\"").append(escapeJson(client.getEmail())).append("\"")
                    .append("}");
                first = false;
            }
            json.append("]");
            sendJsonResponse(exchange, json.toString());
        }
    }

    class AgentHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            StringBuilder json = new StringBuilder("[");
            boolean first = true;
            for (Map.Entry<String, SupportClient> entry : supportAgents.entrySet()) {
                if (!first) json.append(",");
                SupportClient agent = entry.getValue();
                json.append("{")
                    .append("\"id\":\"").append(entry.getKey()).append("\",")
                    .append("\"nom\":\"").append(escapeJson(agent.getNom())).append("\",")
                    .append("\"departement\":\"").append(escapeJson(agent.getSpecialite())).append("\"")
                    .append("}");
                first = false;
            }
            json.append("]");
            sendJsonResponse(exchange, json.toString());
        }
    }

    // ==================== Helper Methods ====================

    private String ticketToJson(Ticket ticket) {
        return "{" +
            "\"id\":" + ticket.getId() + "," +
            "\"titre\":\"" + escapeJson(ticket.getTitre()) + "\"," +
            "\"description\":\"" + escapeJson(ticket.getDescription()) + "\"," +
            "\"priorite\":\"" + escapeJson(ticket.getPriorite()) + "\"," +
            "\"statut\":\"" + ticket.getStatut().getLibelle() + "\"," +
            "\"etat\":\"" + ticket.getState().getNomEtat() + "\"," +
            "\"solution\":\"" + escapeJson(ticket.getSolution() != null ? ticket.getSolution() : "") + "\"," +
            "\"dateCreation\":\"" + ticket.getDateCreation().toString() + "\"" +
            "}";
    }

    private String escapeJson(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }

    private void sendJsonResponse(HttpExchange exchange, String json) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private Map<String, String> parseFormData(HttpExchange exchange) throws IOException {
        Map<String, String> params = new HashMap<>();
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        
        if (!body.isEmpty()) {
            for (String param : body.split("&")) {
                String[] pair = param.split("=", 2);
                if (pair.length == 2) {
                    params.put(
                        URLDecoder.decode(pair[0], StandardCharsets.UTF_8),
                        URLDecoder.decode(pair[1], StandardCharsets.UTF_8)
                    );
                }
            }
        }
        return params;
    }

    private String getIndexHtml() {
        return """
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>CRM Support Client - Test Interface</title>
    <style>
        * { box-sizing: border-box; margin: 0; padding: 0; }
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
            min-height: 100vh;
            color: #e0e0e0;
        }
        .header {
            background: linear-gradient(90deg, #0f3460, #533483);
            padding: 20px;
            text-align: center;
            box-shadow: 0 4px 15px rgba(0,0,0,0.3);
        }
        .header h1 { color: #fff; font-size: 1.8rem; }
        .header p { color: #b8b8b8; margin-top: 5px; }
        .container {
            max-width: 1400px;
            margin: 0 auto;
            padding: 20px;
        }
        .grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
            gap: 20px;
            margin-top: 20px;
        }
        .card {
            background: rgba(255,255,255,0.05);
            border-radius: 15px;
            padding: 20px;
            border: 1px solid rgba(255,255,255,0.1);
            backdrop-filter: blur(10px);
        }
        .card h2 {
            color: #e94560;
            margin-bottom: 15px;
            display: flex;
            align-items: center;
            gap: 10px;
        }
        .form-group { margin-bottom: 15px; }
        .form-group label {
            display: block;
            margin-bottom: 5px;
            color: #b8b8b8;
            font-size: 0.9rem;
        }
        .form-group input, .form-group select, .form-group textarea {
            width: 100%;
            padding: 12px;
            border: 1px solid rgba(255,255,255,0.2);
            border-radius: 8px;
            background: rgba(255,255,255,0.1);
            color: #fff;
            font-size: 1rem;
        }
        .form-group input:focus, .form-group select:focus, .form-group textarea:focus {
            outline: none;
            border-color: #e94560;
        }
        button {
            background: linear-gradient(90deg, #e94560, #533483);
            color: white;
            border: none;
            padding: 12px 24px;
            border-radius: 8px;
            cursor: pointer;
            font-size: 1rem;
            transition: transform 0.2s, box-shadow 0.2s;
        }
        button:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 20px rgba(233,69,96,0.4);
        }
        .btn-small {
            padding: 6px 12px;
            font-size: 0.85rem;
            margin: 2px;
        }
        .btn-assign { background: linear-gradient(90deg, #3498db, #2980b9); }
        .btn-resolve { background: linear-gradient(90deg, #27ae60, #219a52); }
        .btn-close { background: linear-gradient(90deg, #e74c3c, #c0392b); }
        .ticket-list { max-height: 400px; overflow-y: auto; }
        .ticket-item {
            background: rgba(255,255,255,0.05);
            border-radius: 10px;
            padding: 15px;
            margin-bottom: 10px;
            border-left: 4px solid #e94560;
        }
        .ticket-item.ouvert { border-left-color: #3498db; }
        .ticket-item.assigne { border-left-color: #f39c12; }
        .ticket-item.en-cours { border-left-color: #9b59b6; }
        .ticket-item.resolu { border-left-color: #27ae60; }
        .ticket-item.ferme { border-left-color: #7f8c8d; }
        .ticket-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 10px;
        }
        .ticket-title { font-weight: bold; color: #fff; }
        .ticket-id { color: #888; font-size: 0.85rem; }
        .ticket-meta {
            display: flex;
            gap: 15px;
            font-size: 0.85rem;
            color: #aaa;
            margin-bottom: 10px;
        }
        .priority-haute { color: #e74c3c; }
        .priority-moyenne { color: #f39c12; }
        .priority-basse { color: #27ae60; }
        .state-badge {
            display: inline-block;
            padding: 3px 10px;
            border-radius: 15px;
            font-size: 0.8rem;
            font-weight: bold;
        }
        .state-ouvert { background: #3498db; color: white; }
        .state-assigne { background: #f39c12; color: white; }
        .state-en-cours { background: #9b59b6; color: white; }
        .state-resolu { background: #27ae60; color: white; }
        .state-ferme { background: #7f8c8d; color: white; }
        .ticket-actions { margin-top: 10px; }
        .event-log {
            max-height: 300px;
            overflow-y: auto;
            font-family: 'Consolas', monospace;
            font-size: 0.85rem;
        }
        .event-item {
            padding: 8px;
            border-bottom: 1px solid rgba(255,255,255,0.1);
        }
        .article-item {
            background: rgba(255,255,255,0.05);
            border-radius: 8px;
            padding: 12px;
            margin-bottom: 10px;
        }
        .article-title {
            color: #e94560;
            font-weight: bold;
            margin-bottom: 5px;
        }
        .article-content { color: #aaa; font-size: 0.9rem; }
        .patterns-info {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 15px;
        }
        .pattern-box {
            background: rgba(255,255,255,0.05);
            border-radius: 10px;
            padding: 15px;
        }
        .pattern-box h3 {
            color: #e94560;
            margin-bottom: 10px;
            font-size: 1rem;
        }
        .pattern-box p { font-size: 0.85rem; color: #aaa; }
        .state-diagram {
            display: flex;
            align-items: center;
            justify-content: center;
            flex-wrap: wrap;
            gap: 10px;
            padding: 15px;
            background: rgba(0,0,0,0.2);
            border-radius: 10px;
            margin-top: 15px;
        }
        .state-node {
            padding: 8px 15px;
            border-radius: 20px;
            font-size: 0.85rem;
            font-weight: bold;
        }
        .arrow { color: #666; }
        .modal {
            display: none;
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(0,0,0,0.7);
            z-index: 1000;
            justify-content: center;
            align-items: center;
        }
        .modal.active { display: flex; }
        .modal-content {
            background: #1a1a2e;
            padding: 30px;
            border-radius: 15px;
            max-width: 400px;
            width: 90%;
        }
        .modal-content h3 { margin-bottom: 20px; color: #e94560; }
        .close-modal {
            background: #555;
            margin-top: 10px;
        }
        ::-webkit-scrollbar { width: 8px; }
        ::-webkit-scrollbar-track { background: rgba(255,255,255,0.1); border-radius: 4px; }
        ::-webkit-scrollbar-thumb { background: #e94560; border-radius: 4px; }
    </style>
</head>
<body>
    <div class="header">
        <h1>CRM Support Client - Interface de Test</h1>
    </div>

    <div class="container">
        <div class="grid">
            <!-- Create Ticket Form -->
            <div class="card">
                <h2>Creer un Ticket</h2>
                <form id="createTicketForm">
                    <div class="form-group">
                        <label>Client</label>
                        <select id="clientSelect" required>
                            <!-- Populated dynamically -->
                        </select>
                    </div>
                    <div class="form-group">
                        <label>Titre</label>
                        <input type="text" id="ticketTitle" placeholder="Titre du ticket" required>
                    </div>
                    <div class="form-group">
                        <label>Description</label>
                        <textarea id="ticketDesc" rows="3" placeholder="Décrivez le problème..."></textarea>
                    </div>
                    <div class="form-group">
                        <label>Priorite</label>
                        <select id="ticketPriority">
                            <option value="Basse">Basse</option>
                            <option value="Moyenne" selected>Moyenne</option>
                            <option value="Haute">Haute</option>
                        </select>
                    </div>
                    <button type="submit">Creer le Ticket</button>
                </form>
            </div>

            <!-- Tickets List -->
            <div class="card">
                <h2>Liste des Tickets</h2>
                <div class="ticket-list" id="ticketList">
                    <p style="color: #888; text-align: center;">Aucun ticket cree</p>
                </div>
            </div>

            <!-- Event Log -->
            <div class="card">
                <h2>Journal des Evenements</h2>
                <div class="event-log" id="eventLog">
                    <p style="color: #888; text-align: center;">Aucun evenement</p>
                </div>
            </div>

            <!-- Knowledge Base -->
            <div class="card">
                <h2>Base de Connaissances</h2>
                <div id="articleList">
                    <!-- Populated dynamically -->
                </div>
            </div>
        </div>
    </div>

    <!-- Assign Modal -->
    <div class="modal" id="assignModal">
        <div class="modal-content">
            <h3>Assigner le Ticket</h3>
            <input type="hidden" id="assignTicketId">
            <div class="form-group">
                <label>Agent Support</label>
                <select id="agentSelect">
                    <!-- Populated dynamically -->
                </select>
            </div>
            <button onclick="confirmAssign()">Assigner</button>
            <button class="close-modal" onclick="closeModal('assignModal')">Annuler</button>
        </div>
    </div>

    <!-- Resolve Modal -->
    <div class="modal" id="resolveModal">
        <div class="modal-content">
            <h3>Resoudre le Ticket</h3>
            <input type="hidden" id="resolveTicketId">
            <div class="form-group">
                <label>Solution</label>
                <textarea id="solutionText" rows="4" placeholder="Decrivez la solution..."></textarea>
            </div>
            <button onclick="confirmResolve()">Résoudre</button>
            <button class="close-modal" onclick="closeModal('resolveModal')">Annuler</button>
        </div>
    </div>

    <script>
        // Load initial data
        document.addEventListener('DOMContentLoaded', () => {
            loadClients();
            loadAgents();
            loadTickets();
            loadArticles();
            loadEvents();
            
            // Refresh events every 2 seconds
            setInterval(loadEvents, 2000);
        });

        async function loadClients() {
            const response = await fetch('/api/clients');
            const clients = await response.json();
            const select = document.getElementById('clientSelect');
            select.innerHTML = clients.map(c => 
                `<option value="${c.id}">${c.nom} (${c.email})</option>`
            ).join('');
        }

        async function loadAgents() {
            const response = await fetch('/api/agents');
            const agents = await response.json();
            const select = document.getElementById('agentSelect');
            select.innerHTML = agents.map(a => 
                `<option value="${a.id}">${a.nom} - ${a.departement}</option>`
            ).join('');
        }

        async function loadTickets() {
            const response = await fetch('/api/tickets');
            const tickets = await response.json();
            const container = document.getElementById('ticketList');
            
            if (tickets.length === 0) {
                container.innerHTML = '<p style="color: #888; text-align: center;">Aucun ticket créé</p>';
                return;
            }
            
            container.innerHTML = tickets.map(t => {
                const stateClass = t.etat.toLowerCase().replace(' ', '-');
                const priorityClass = 'priority-' + t.priorite.toLowerCase();
                const canAssign = t.etat === 'Ouvert';
                const canResolve = t.etat === 'Assigné' || t.etat === 'En Cours';
                const canClose = t.etat === 'Résolu';
                
                return `
                    <div class="ticket-item ${stateClass}">
                        <div class="ticket-header">
                            <span class="ticket-title">${t.titre}</span>
                            <span class="ticket-id">#${t.id}</span>
                        </div>
                        <div class="ticket-meta">
                            <span class="${priorityClass}">● ${t.priorite}</span>
                            <span class="state-badge state-${stateClass}">${t.etat}</span>
                        </div>
                        <p style="font-size: 0.9rem; color: #aaa; margin-bottom: 10px;">${t.description}</p>
                        ${t.solution ? `<p style="font-size: 0.85rem; color: #27ae60;">Solution: ${t.solution}</p>` : ''}
                        <div class="ticket-actions">
                            ${canAssign ? `<button class="btn-small btn-assign" onclick="showAssignModal(${t.id})">Assigner</button>` : ''}
                            ${canResolve ? `<button class="btn-small btn-resolve" onclick="showResolveModal(${t.id})">Resoudre</button>` : ''}
                            ${canClose ? `<button class="btn-small btn-close" onclick="closeTicket(${t.id})">Fermer</button>` : ''}
                        </div>
                    </div>
                `;
            }).join('');
        }

        async function loadArticles() {
            const response = await fetch('/api/articles');
            const articles = await response.json();
            const container = document.getElementById('articleList');
            
            container.innerHTML = articles.map(a => `
                <div class="article-item">
                    <div class="article-title">${a.titre}</div>
                    <div class="article-content">${a.contenu}</div>
                </div>
            `).join('');
        }

        async function loadEvents() {
            const response = await fetch('/api/events');
            const events = await response.json();
            const container = document.getElementById('eventLog');
            
            if (events.length === 0) {
                container.innerHTML = '<p style="color: #888; text-align: center;">Aucun événement</p>';
                return;
            }
            
            container.innerHTML = events.map(e => 
                `<div class="event-item">${e}</div>`
            ).join('');
        }

        // Create Ticket
        document.getElementById('createTicketForm').addEventListener('submit', async (e) => {
            e.preventDefault();
            
            const formData = new URLSearchParams({
                clientId: document.getElementById('clientSelect').value,
                titre: document.getElementById('ticketTitle').value,
                description: document.getElementById('ticketDesc').value,
                priorite: document.getElementById('ticketPriority').value
            });
            
            await fetch('/api/tickets', {
                method: 'POST',
                body: formData
            });
            
            document.getElementById('ticketTitle').value = '';
            document.getElementById('ticketDesc').value = '';
            
            loadTickets();
            loadEvents();
        });

        // Modals
        function showAssignModal(ticketId) {
            document.getElementById('assignTicketId').value = ticketId;
            document.getElementById('assignModal').classList.add('active');
        }

        function showResolveModal(ticketId) {
            document.getElementById('resolveTicketId').value = ticketId;
            document.getElementById('resolveModal').classList.add('active');
        }

        function closeModal(modalId) {
            document.getElementById(modalId).classList.remove('active');
        }

        async function confirmAssign() {
            const ticketId = document.getElementById('assignTicketId').value;
            const agentId = document.getElementById('agentSelect').value;
            
            await fetch('/api/tickets/action', {
                method: 'POST',
                body: new URLSearchParams({ ticketId, action: 'assigner', agentId })
            });
            
            closeModal('assignModal');
            loadTickets();
            loadEvents();
        }

        async function confirmResolve() {
            const ticketId = document.getElementById('resolveTicketId').value;
            const solution = document.getElementById('solutionText').value;
            
            await fetch('/api/tickets/action', {
                method: 'POST',
                body: new URLSearchParams({ ticketId, action: 'resoudre', solution })
            });
            
            closeModal('resolveModal');
            document.getElementById('solutionText').value = '';
            loadTickets();
            loadEvents();
        }

        async function closeTicket(ticketId) {
            await fetch('/api/tickets/action', {
                method: 'POST',
                body: new URLSearchParams({ ticketId, action: 'fermer' })
            });
            
            loadTickets();
            loadEvents();
        }
    </script>
</body>
</html>
""";
    }

    public static void main(String[] args) {
        try {
            int port = 8080;
            if (args.length > 0) {
                port = Integer.parseInt(args[0]);
            }
            WebServer server = new WebServer(port);
            server.start();
        } catch (IOException e) {
            System.err.println("Failed to start server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
