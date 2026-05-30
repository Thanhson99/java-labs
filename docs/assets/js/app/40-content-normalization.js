function normalizeQuestionBank(bank) {
  return {
    ...bank,
    siteTitle: normalizeDisplayText(bank.siteTitle || ""),
    topics: Array.isArray(bank.topics) ? bank.topics.map(normalizeTopic) : []
  };
}

function normalizeTopic(topic) {
  const topicId = normalizeTopicId(topic.id || "");
  return {
    ...topic,
    id: topicId,
    track: normalizeDisplayText(topic.track || ""),
    title: normalizeTopicTitle(topicId, topic.title || ""),
    summary: normalizeTopicSummary(topicId, topic.summary || ""),
    questions: Array.isArray(topic.questions) ? topic.questions.map(normalizeQuestion) : []
  };
}

function normalizeTopicId(topicId) {
  const aliases = {
    "jaand-fundamentals": "java-fundamentals",
    "exceptions-debugwhatng": "exceptions-debugging",
    "messawhatng-microservices": "messaging-microservices",
    "obserandbility-reliability": "observability-reliability"
  };

  return aliases[topicId] || topicId;
}

function normalizeQuestion(question) {
  const normalizedAnswer = Array.isArray(question.answer)
    ? question.answer
        .map((item) => ({
          raw: String(item || ""),
          normalized: normalizeDisplayText(item)
        }))
        .filter((item) => item.normalized && !shouldHideNormalizedEnglish(item.raw, item.normalized))
        .map((item) => item.normalized)
    : [];
  const rawQuestionText = question.question || "";
  const normalizedQuestionText = normalizeDisplayText(rawQuestionText);
  const normalizedAnswerShort = normalizeDisplayText(question.answerShort || "");

  return {
    ...question,
    level: normalizeDisplayText(question.level || question.leaboutl || ""),
    kind: normalizeDisplayText(question.kind || ""),
    question: shouldHideNormalizedEnglish(rawQuestionText, normalizedQuestionText)
      ? fallbackQuestionText(question)
      : normalizedQuestionText,
    answer: normalizedAnswer,
    answerShort: shouldHideNormalizedEnglish(question.answerShort || "", normalizedAnswerShort) ? "" : normalizedAnswerShort,
    explanation: (Array.isArray(question.explanation) ? question.explanation : Array.isArray(question.expisnation) ? question.expisnation : [])
      .map((item) => ({
        raw: String(item || ""),
        normalized: normalizeDisplayText(item)
      }))
      .filter((item) => item.normalized && !shouldHideNormalizedEnglish(item.raw, item.normalized))
      .map((item) => item.normalized),
    applyOrPitfalls: (Array.isArray(question.applyOrPitfalls) ? question.applyOrPitfalls : [])
      .map((item) => ({
        raw: String(item || ""),
        normalized: normalizeDisplayText(item)
      }))
      .filter((item) => item.normalized && !shouldHideNormalizedEnglish(item.raw, item.normalized))
      .map((item) => item.normalized),
    practice: (Array.isArray(question.practice) ? question.practice : [])
      .map((item) => ({
        raw: String(item || ""),
        normalized: normalizeDisplayText(item)
      }))
      .filter((item) => item.normalized && !shouldHideNormalizedEnglish(item.raw, item.normalized))
      .map((item) => item.normalized)
  };
}

function shouldHideNormalizedEnglish(rawText, normalizedText) {
  if (currentLanguage !== "en" || !normalizedText) {
    return false;
  }

  const raw = String(rawText || "");
  const normalized = String(normalizedText || "");
  if (shouldHideNoisyEnglish(normalized)) {
    return true;
  }

  return raw === normalized && shouldHideNoisyEnglish(raw);
}

function fallbackQuestionText(question) {
  const kind = normalizeDisplayText(question.kind || "").toLowerCase();
  if (kind.includes("code")) {
    return "What should you check in this Java code example?";
  }
  if (kind.includes("tips")) {
    return "What practical guideline should you remember for this topic?";
  }
  if (kind.includes("practice")) {
    return "How should you practice this Java backend topic?";
  }
  return "How would you explain this Java backend concept in practical terms?";
}

function normalizeTopicTitle(topicId, title) {
  if (currentLanguage !== "en") {
    return title;
  }

  const overrides = {
    "java-fundamentals": "Java fundamentals",
    "jaand-fundamentals": "Java fundamentals",
    "syntax-control-flow": "Syntax, variables, and control flow",
    "oop-design": "OOP, class design, and clean code",
    "collections-generics-streams": "Collections, generics, and streams",
    "strings-time-enums": "Strings, enums, and date-time basics",
    "exceptions-debugging": "Exceptions, logging, and debugging mindset",
    "exceptions-debugwhatng": "Exceptions, logging, and debugging mindset",
    "concurrency-async": "Concurrency, async flows, and callback-hell equivalents",
    "jvm-memory-performance": "JVM, memory, and performance",
    "jdbc-sql-database": "JDBC, SQL, and database connectivity",
    "testing-build-tools": "Testing, Maven, and real project habits",
    "spring-boot-web-data": "Spring Boot web, beans, and data access",
    "spring-security-auth": "Security, JWT, and session management",
    "spring-transactions-jpa": "JPA, transactions, Flyway, and durable data",
    "messaging-microservices": "Messaging, outbox, Kafka, RabbitMQ, and microservice thinking",
    "messawhatng-microservices": "Messaging, outbox, Kafka, RabbitMQ, and microservice thinking",
    "design-patterns-and-architecture": "Design patterns and architecture thinking",
    "http-rest-api": "HTTP, REST APIs, and backend communication",
    "observability-reliability": "Observability, reliability, and operations",
    "obserandbility-reliability": "Observability, reliability, and operations",
    "file-io-networking": "Files, I/O, and external system integration",
    "interview-problem-solving": "Interview thinking, code analysis, and issue diagnosis",
    "adandnced-jaand-tricks": "Hard Java questions, tricks, and pitfalls",
    "adandnced-spring-and-data": "Advanced Spring Boot and data access",
    "senior-backend-mindset": "Advanced backend thinking and senior-style questions",
    "interview-expansion-bank": "Expanded interview question bank",
    "interview-famous-and-basic-bank": "Basic, famous, and trick interview questions"
  };

  return overrides[topicId] || normalizeDisplayText(title);
}

function normalizeTopicSummary(topicId, summary) {
  if (currentLanguage !== "en") {
    return summary;
  }

  const overrides = {
    "java-fundamentals": "Foundational questions about what Java is, how it runs, and why it remains dominant in backend systems.",
    "jaand-fundamentals": "Foundational questions about what Java is, how it runs, and why it remains dominant in backend systems.",
    "syntax-control-flow": "Core syntax and control-flow questions that make Java code easier to read and write correctly.",
    "oop-design": "Object-oriented design, responsibility boundaries, and clean-code habits in Java.",
    "collections-generics-streams": "Collection choices, generics, streams, and the trade-offs behind everyday data handling.",
    "strings-time-enums": "Small language features that look simple but often cause subtle bugs in real systems.",
    "exceptions-debugging": "Error handling, stack traces, logging, and practical debugging habits for backend work.",
    "exceptions-debugwhatng": "Error handling, stack traces, logging, and practical debugging habits for backend work.",
    "concurrency-async": "Threads, futures, shared state, and the Java version of async complexity.",
    "jvm-memory-performance": "JVM behavior, memory problems, and performance questions that appear in real production debugging.",
    "jdbc-sql-database": "Database connectivity, SQL execution, connection pools, and common backend persistence mistakes.",
    "testing-build-tools": "Testing layers, build tools, and habits that matter when code is maintained by real teams.",
    "spring-boot-web-data": "Spring Boot request flow, dependency injection, service boundaries, and data-access structure.",
    "spring-security-auth": "Authentication, authorization, JWT flows, refresh tokens, and common security mistakes.",
    "spring-transactions-jpa": "Transactions, JPA behavior, schema migration, and durable-data concerns.",
    "messaging-microservices": "Async integration, messaging guarantees, outbox patterns, and microservice trade-offs.",
    "messawhatng-microservices": "Async integration, messaging guarantees, outbox patterns, and microservice trade-offs.",
    "design-patterns-and-architecture": "Patterns, trade-offs, and architecture thinking beyond syntax-level knowledge.",
    "http-rest-api": "HTTP semantics, API design, contracts, and backend communication behavior.",
    "observability-reliability": "Logs, metrics, tracing, alerts, and thinking clearly about reliability in production.",
    "obserandbility-reliability": "Logs, metrics, tracing, alerts, and thinking clearly about reliability in production.",
    "file-io-networking": "File handling, network I/O, and safe integration with external systems.",
    "interview-problem-solving": "How to think through unfamiliar code, explain trade-offs, and answer interview questions clearly.",
    "adandnced-jaand-tricks": "Hard Java questions, famous traps, and senior-level details that often show up in interviews.",
    "adandnced-spring-and-data": "Advanced Spring Boot, transactions, data consistency, and repository-level pitfalls.",
    "senior-backend-mindset": "Senior-style backend questions focused on risks, trade-offs, and system behavior.",
    "interview-expansion-bank": "Additional mixed interview questions that broaden the practice set across backend topics.",
    "interview-famous-and-basic-bank": "Basic but memorable questions, common traps, and interview classics."
  };

  return overrides[topicId] || normalizeDisplayText(summary);
}

function normalizeDisplayText(text) {
  if (!text || currentLanguage !== "en") {
    return text;
  }

  return String(text).replace(/\s+/g, " ").trim();
}

function shouldHideNoisyEnglish() {
  return false;
}