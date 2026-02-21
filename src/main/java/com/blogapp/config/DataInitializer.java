package com.blogapp.config;

import com.blogapp.blog.entity.BlogPost;
import com.blogapp.blog.enums.BlogStatus;
import com.blogapp.blog.repository.BlogPostRepository;
import com.blogapp.comment.entity.BlogComment;
import com.blogapp.comment.enums.CommentStatus;
import com.blogapp.comment.repository.CommentRepository;
import com.blogapp.section.entity.Section;
import com.blogapp.section.entity.Subsection;
import com.blogapp.section.repository.SectionRepository;
import com.blogapp.section.repository.SubsectionRepository;
import com.blogapp.subscriber.entity.Subscriber;
import com.blogapp.subscriber.enums.SubscriberStatus;
import com.blogapp.subscriber.repository.SubscriberRepository;
import com.blogapp.user.entity.User;
import com.blogapp.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final BlogPostRepository blogPostRepository;
    private final CommentRepository commentRepository;
    private final SubscriberRepository subscriberRepository;
    private final SectionRepository sectionRepository;
    private final SubsectionRepository subsectionRepository;
    private final UserRepository userRepository;

    @Override
    public void run(String... args) {
        seedSections();

        if (blogPostRepository.count() > 0) {
            log.info("Database already contains blog data. Skipping blog initialization.");
            return;
        }

        log.info("Initializing database with seed data...");

        List<BlogPost> blogs = createSampleBlogs();

        List<Section> sections = sectionRepository.findAll();
        List<Subsection> subsections = subsectionRepository.findAll();

        List<BlogPost> finalBlogs = new java.util.ArrayList<>();

        if (!sections.isEmpty() && !subsections.isEmpty()) {
            for (int i = 0; i < subsections.size() * 2; i++) {
                // Cycle through subsections
                Subsection sub = subsections.get(i % subsections.size());

                // Cycle through template blogs
                BlogPost template = blogs.get(i % blogs.size());
                String suffix = i >= blogs.size() ? " (Vol. " + (i + 1) + ")" : "";

                BlogPost newBlog = BlogPost.builder()
                        .title(template.getTitle() + suffix)
                        .slug(template.getSlug() + "-" + i)
                        .excerpt(template.getExcerpt())
                        .contentHtml(template.getContentHtml())
                        .featuredImageUrl(template.getFeaturedImageUrl())
                        .publishedAt(template.getPublishedAt() != null ? template.getPublishedAt().minusDays(i)
                                : LocalDateTime.now().minusDays(i))
                        .status(template.getStatus())
                        .internalRating(template.getInternalRating())
                        .tags(template.getTags())
                        .authorUserId(template.getAuthorUserId())
                        .authorName(template.getAuthorName())
                        .viewsCount(template.getViewsCount() + i * 10)
                        .likesCount(template.getLikesCount() + i * 2)
                        .commentsCount(template.getCommentsCount())
                        .sectionId(sub.getSectionId())
                        .subsectionId(sub.getId())
                        .build();

                finalBlogs.add(newBlog);
            }
        }

        blogPostRepository.saveAll(finalBlogs);
        log.info("Created {} sample blogs", finalBlogs.size());

        if (!finalBlogs.isEmpty()) {
            List<BlogComment> comments = createSampleComments(finalBlogs.get(0).getId());
            commentRepository.saveAll(comments);
            log.info("Created {} sample comments", comments.size());
        }

        List<Subscriber> subscribers = createSampleSubscribers();
        subscriberRepository.saveAll(subscribers);
        log.info("Created {} sample subscribers", subscribers.size());

        if (userRepository.count() == 0) {
            List<User> sampleUsers = createSampleUsers();
            userRepository.saveAll(sampleUsers);
            log.info("Created {} sample users", sampleUsers.size());
        }

        log.info("Database initialization completed successfully!");
    }

    private List<User> createSampleUsers() {
        return Arrays.asList(
                User.builder()
                        .name("Alice Viewer")
                        .email("alice@example.com")
                        .emailVerifiedAt(LocalDateTime.now())
                        .createdAt(LocalDateTime.now())
                        .build(),
                User.builder()
                        .name("Bob Premium")
                        .email("bob@example.com")
                        .emailVerifiedAt(LocalDateTime.now())
                        .createdAt(LocalDateTime.now())
                        .build());
    }

    private List<BlogPost> createSampleBlogs() {
        LocalDateTime now = LocalDateTime.now();

        // ── Blog 1: Rich content with EVERYTHING ──
        BlogPost blog1 = BlogPost.builder()
                .title("The Complete Guide to Modern Java Development")
                .slug("complete-guide-modern-java-development")
                .excerpt(
                        "Master Java 17+ features including records, sealed classes, pattern matching, and virtual threads with practical examples.")
                .contentHtml(
                        """
                                                        <h1>The Complete Guide to Modern Java Development</h1>
                                                        <p>Java has evolved dramatically in recent years. This guide covers the most impactful features from Java 17 through 21 that every developer should know.</p>

                                                        <figure class="blog-figure">
                                                            <img src="https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=800" alt="Coding on laptop" class="blog-img" />
                                                        </figure>

                                                        <h2>1. Records — Data Classes Made Simple</h2>
                                                        <p>Records eliminate the boilerplate of data-carrying classes. Instead of writing getters, setters, equals, hashCode, and toString — you write <strong>one line</strong>:</p>

                                                        <pre><code>public record BlogPost(String title, String author, LocalDate publishedAt) {
                                    // Compact constructor for validation
                                    public BlogPost {
                                        Objects.requireNonNull(title, "Title cannot be null");
                                        Objects.requireNonNull(author, "Author cannot be null");
                                    }
                                }</code></pre>

                                                        <blockquote>
                                                            <p>"Records are the single most productivity-boosting feature Java has added in the last decade." — Brian Goetz, Java Architect</p>
                                                        </blockquote>

                                                        <h2>2. Sealed Classes</h2>
                                                        <p>Sealed classes restrict which other classes or interfaces may extend or implement them:</p>

                                                        <pre><code>public sealed interface Shape permits Circle, Rectangle, Triangle {
                                    double area();
                                }

                                public record Circle(double radius) implements Shape {
                                    public double area() { return Math.PI * radius * radius; }
                                }

                                public record Rectangle(double w, double h) implements Shape {
                                    public double area() { return w * h; }
                                }</code></pre>

                                                        <h2>3. Pattern Matching</h2>
                                                        <p>Pattern matching makes type checks and casts <em>incredibly</em> clean:</p>

                                                        <pre><code>// Before Java 16
                                if (obj instanceof String) {
                                    String s = (String) obj;
                                    System.out.println(s.length());
                                }

                                // After Java 16+
                                if (obj instanceof String s) {
                                    System.out.println(s.length());
                                }</code></pre>

                                                        <h3>Switch Pattern Matching (Java 21)</h3>
                                                        <pre><code>String describe(Object obj) {
                                    return switch (obj) {
                                        case Integer i when i > 0 -> "Positive integer: " + i;
                                        case String s             -> "String of length " + s.length();
                                        case null                 -> "null value";
                                        default                   -> "Unknown: " + obj;
                                    };
                                }</code></pre>

                                                        <h2>4. Virtual Threads (Project Loom)</h2>
                                                        <p>Virtual threads allow you to create <strong>millions</strong> of lightweight threads:</p>

                                                        <pre><code>try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
                                    IntStream.range(0, 10_000).forEach(i ->
                                        executor.submit(() -> {
                                            Thread.sleep(Duration.ofSeconds(1));
                                            return i;
                                        })
                                    );
                                }</code></pre>

                                                        <h2>Key Takeaways</h2>
                                                        <ul>
                                                            <li><strong>Records</strong> eliminate data class boilerplate</li>
                                                            <li><strong>Sealed classes</strong> provide exhaustive type hierarchies</li>
                                                            <li><strong>Pattern matching</strong> simplifies type-based logic</li>
                                                            <li><strong>Virtual threads</strong> enable massive concurrency</li>
                                                        </ul>

                                                        <hr />

                                                        <p><em>What's your favorite modern Java feature? Share your thoughts in the comments below!</em></p>
                                                        """)
                .featuredImageUrl("https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=800")
                .tags(Arrays.asList("java", "programming", "tutorial", "backend"))
                .authorName("Sarah Johnson")
                .authorEmail("sarah.johnson@example.com")
                .authorMobile("+1234567890")
                .status(BlogStatus.PUBLISHED)
                .year(now.getYear()).month(now.getMonthValue())
                .publishedAt(now.minusDays(2))
                .submittedAt(now.minusDays(3))
                .likesCount(142).dislikesCount(5).commentsCount(3).viewsCount(856)
                .emailSent(true)
                .internalRating(9) // Premium trigger
                .contentPart1Html(
                        "<h1>The Complete Guide to Modern Java Development</h1><p>Java has evolved dramatically in recent years. This guide covers the most impactful features from Java 17 through 21 that every developer should know.</p><h2>1. Records — Data Classes Made Simple</h2><p>Records eliminate the boilerplate of data-carrying classes...</p>")
                .contentPart2Html(
                        "<h2>2. Sealed Classes</h2><p>Sealed classes restrict which other classes or interfaces may extend or implement them.</p><h2>3. Pattern Matching</h2><p>Pattern matching makes type checks and casts <em>incredibly</em> clean.</p><h2>4. Virtual Threads (Project Loom)</h2><p>Virtual threads allow you to create <strong>millions</strong> of lightweight threads.</p>")
                .build();

        // ── Blog 2: CSS & Design ──
        BlogPost blog2 = BlogPost.builder()
                .title("Mastering CSS Grid and Flexbox in 2026")
                .slug("mastering-css-grid-flexbox-2026")
                .excerpt(
                        "A visual deep-dive into CSS Grid and Flexbox with real-world layout patterns you can use today.")
                .contentHtml(
                        """
                                                        <h1>Mastering CSS Grid and Flexbox</h1>
                                                        <p>Modern CSS layout is <strong>powerful</strong> and <em>intuitive</em> once you understand the mental models behind Grid and Flexbox.</p>

                                                        <h2>Flexbox — One-Dimensional Layouts</h2>
                                                        <p>Flexbox excels at distributing space along a <u>single axis</u>:</p>

                                                        <pre><code>.nav {
                                    display: flex;
                                    justify-content: space-between;
                                    align-items: center;
                                    gap: 1rem;
                                }

                                .nav-item {
                                    flex: 0 0 auto;
                                }

                                .nav-spacer {
                                    flex: 1; /* pushes remaining items to the right */
                                }</code></pre>

                                                        <h2>CSS Grid — Two-Dimensional Layouts</h2>
                                                        <p>Grid handles <strong>rows and columns</strong> simultaneously:</p>

                                                        <pre><code>.dashboard {
                                    display: grid;
                                    grid-template-columns: 250px 1fr 300px;
                                    grid-template-rows: auto 1fr auto;
                                    grid-template-areas:
                                        "sidebar header  aside"
                                        "sidebar content aside"
                                        "sidebar footer  aside";
                                    gap: 1.5rem;
                                    min-height: 100vh;
                                }</code></pre>

                                                        <blockquote>
                                                            <p>Use Flexbox for components, Grid for page layouts. They complement each other beautifully.</p>
                                                        </blockquote>

                                                        <h3>Responsive Without Media Queries</h3>
                                                        <pre><code>.card-grid {
                                    display: grid;
                                    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
                                    gap: 1.5rem;
                                }</code></pre>

                                                        <p>This single declaration creates a <em>fully responsive</em> card grid that adapts to any screen size!</p>

                                                        <h2>Summary</h2>
                                                        <ol>
                                                            <li>Use <strong>Flexbox</strong> for navigation bars, card layouts, centering</li>
                                                            <li>Use <strong>Grid</strong> for page templates, dashboards, complex layouts</li>
                                                            <li>Combine both for maximum flexibility</li>
                                                        </ol>
                                                        """)
                .featuredImageUrl("https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=800")
                .tags(Arrays.asList("css", "frontend", "web-design", "tutorial"))
                .authorName("Emily Rodriguez")
                .authorEmail("emily.rodriguez@example.com")
                .authorMobile("+1234567891")
                .status(BlogStatus.PUBLISHED)
                .year(now.getYear()).month(now.getMonthValue())
                .publishedAt(now.minusDays(5))
                .submittedAt(now.minusDays(6))
                .likesCount(87).dislikesCount(3).commentsCount(0).viewsCount(534)
                .emailSent(true)
                .build();

        // ── Blog 3: React Hooks ──
        BlogPost blog3 = BlogPost.builder()
                .title("React Hooks Deep Dive: useEffect, useMemo, and useCallback")
                .slug("react-hooks-deep-dive")
                .excerpt("Understand when and how to use React's most important hooks to write performant components.")
                .contentHtml(
                        """
                                                        <h1>React Hooks Deep Dive</h1>
                                                        <p>React hooks changed how we write components. But misusing them is one of the most common sources of bugs and performance issues.</p>

                                                        <h2>useEffect — Managing Side Effects</h2>
                                                        <pre><code>useEffect(() => {
                                    const controller = new AbortController();

                                    async function fetchData() {
                                        try {
                                            const res = await fetch('/api/blogs', {
                                                signal: controller.signal
                                            });
                                            const data = await res.json();
                                            setBlogs(data);
                                        } catch (err) {
                                            if (err.name !== 'AbortError') {
                                                setError(err.message);
                                            }
                                        }
                                    }

                                    fetchData();
                                    return () => controller.abort(); // cleanup!
                                }, []);</code></pre>

                                                        <blockquote>
                                                            <p>Always clean up subscriptions, timers, and network requests in useEffect's return function.</p>
                                                        </blockquote>

                                                        <h2>useMemo — Expensive Computations</h2>
                                                        <p>Only use <code>useMemo</code> when you have a genuinely <strong>expensive computation</strong>:</p>
                                                        <pre><code>const filteredBlogs = useMemo(() => {
                                    return blogs.filter(blog =>
                                        blog.title.toLowerCase().includes(search.toLowerCase())
                                    );
                                }, [blogs, search]);</code></pre>

                                                        <h2>useCallback — Stable Function References</h2>
                                                        <pre><code>const handleDelete = useCallback((id) => {
                                    setBlogs(prev => prev.filter(blog => blog.id !== id));
                                }, []);</code></pre>

                                                        <h3>When NOT to Optimize</h3>
                                                        <ul>
                                                            <li>Simple calculations that run in microseconds</li>
                                                            <li>Functions that aren't passed to memoized children</li>
                                                            <li>Components that rarely re-render</li>
                                                        </ul>

                                                        <hr />

                                                        <p><em>Premature optimization is the root of all evil — Donald Knuth</em></p>
                                                        """)
                .featuredImageUrl("https://images.unsplash.com/photo-1633356122544-f134324a6cee?w=800")
                .tags(Arrays.asList("react", "javascript", "hooks", "frontend"))
                .authorName("Michael Chen")
                .authorEmail("michael.chen@example.com")
                .authorMobile("+1234567892")
                .status(BlogStatus.PUBLISHED)
                .year(now.getYear()).month(now.getMonthValue())
                .publishedAt(now.minusDays(8))
                .submittedAt(now.minusDays(9))
                .likesCount(156).dislikesCount(4).commentsCount(0).viewsCount(923)
                .emailSent(true)
                .build();

        // ── Blog 4: REST API Design ──
        BlogPost blog4 = BlogPost.builder()
                .title("RESTful API Design: Patterns and Anti-Patterns")
                .slug("restful-api-design-patterns")
                .excerpt(
                        "Learn the core principles of REST architecture with real-world examples of good and bad API design.")
                .contentHtml(
                        """
                                                        <h2>What Makes a Great API?</h2>
                                                        <p>A well-designed API is <strong>intuitive</strong>, <em>consistent</em>, and easy to consume. Let's look at what separates great APIs from mediocre ones.</p>

                                                        <h2>✅ Good Patterns</h2>
                                                        <h3>Resource Naming</h3>
                                                        <pre><code>GET    /api/v1/blogs          // list all blogs
                                GET    /api/v1/blogs/42       // get blog by id
                                POST   /api/v1/blogs          // create a blog
                                PUT    /api/v1/blogs/42       // update blog
                                DELETE /api/v1/blogs/42       // delete blog
                                GET    /api/v1/blogs/42/comments  // nested resource</code></pre>

                                                        <h3>Pagination</h3>
                                                        <pre><code>{
                                    "content": [...],
                                    "page": 0,
                                    "totalPages": 5,
                                    "totalElements": 48,
                                    "size": 10
                                }</code></pre>

                                                        <h2>❌ Anti-Patterns</h2>
                                                        <pre><code>GET /api/getBlogs         ❌ Verb in URL
                                GET /api/blog/delete/42   ❌ Action in URL
                                POST /api/blogs/42        ❌ POST for update</code></pre>

                                                        <blockquote><p>The best API is the one your developers can use without reading the docs.</p></blockquote>

                                                        <h2>HTTP Status Codes</h2>
                                                        <ol>
                                                            <li><strong>200 OK</strong> — Successful GET/PUT</li>
                                                            <li><strong>201 Created</strong> — Successful POST</li>
                                                            <li><strong>204 No Content</strong> — Successful DELETE</li>
                                                            <li><strong>400 Bad Request</strong> — Validation errors</li>
                                                            <li><strong>404 Not Found</strong> — Resource doesn't exist</li>
                                                            <li><strong>409 Conflict</strong> — Duplicate resource</li>
                                                        </ol>
                                                        """)
                .featuredImageUrl("https://images.unsplash.com/photo-1558494949-ef010cbdcc31?w=800")
                .tags(Arrays.asList("rest", "api", "web-development", "architecture"))
                .authorName("David Park")
                .authorEmail("david.park@example.com")
                .authorMobile("+1234567893")
                .status(BlogStatus.PUBLISHED)
                .year(now.minusMonths(1).getYear()).month(now.minusMonths(1).getMonthValue())
                .publishedAt(now.minusDays(35))
                .submittedAt(now.minusDays(36))
                .likesCount(63).dislikesCount(1).commentsCount(0).viewsCount(420)
                .emailSent(true)
                .build();

        // ── Blog 5: Docker ──
        BlogPost blog5 = BlogPost.builder()
                .title("Docker from Zero to Production")
                .slug("docker-zero-to-production")
                .excerpt(
                        "Everything you need to know about Docker — from your first container to production deployments.")
                .contentHtml(
                        """
                                                        <h1>Docker from Zero to Production</h1>

                                                        <h2>What is Docker?</h2>
                                                        <p>Docker packages applications into <strong>containers</strong> — lightweight, portable units that include everything needed to run your app.</p>

                                                        <figure class="blog-figure">
                                                            <img src="https://images.unsplash.com/photo-1605745341112-85968b19335b?w=800" alt="Container shipping" class="blog-img" />
                                                        </figure>

                                                        <h2>Your First Dockerfile</h2>
                                                        <pre><code>FROM openjdk:17-slim
                                WORKDIR /app
                                COPY target/blog-app.jar app.jar
                                EXPOSE 8080
                                ENTRYPOINT ["java", "-jar", "app.jar"]</code></pre>

                                                        <h2>Docker Compose for Local Development</h2>
                                                        <pre><code>version: '3.8'
                                services:
                                  app:
                                    build: .
                                    ports:
                                      - "8080:8080"
                                    depends_on:
                                      - mongodb
                                    environment:
                                      SPRING_DATA_MONGODB_URI: mongodb://mongodb:27017/blogdb

                                  mongodb:
                                    image: mongo:7
                                    ports:
                                      - "27017:27017"
                                    volumes:
                                      - mongo_data:/data/db

                                volumes:
                                  mongo_data:</code></pre>

                                                        <blockquote><p>Docker Compose lets you define your entire stack in one file. No more "works on my machine" excuses!</p></blockquote>

                                                        <h3>Essential Docker Commands</h3>
                                                        <ul>
                                                            <li><code>docker build -t myapp .</code> — Build an image</li>
                                                            <li><code>docker run -p 8080:8080 myapp</code> — Run a container</li>
                                                            <li><code>docker compose up -d</code> — Start all services</li>
                                                            <li><code>docker compose logs -f</code> — View logs</li>
                                                        </ul>
                                                        """)
                .featuredImageUrl("https://images.unsplash.com/photo-1605745341112-85968b19335b?w=800")
                .tags(Arrays.asList("docker", "containers", "devops", "deployment"))
                .authorName("James Martinez")
                .authorEmail("james.martinez@example.com")
                .authorMobile("+1234567894")
                .status(BlogStatus.PUBLISHED)
                .year(now.getYear()).month(now.getMonthValue())
                .publishedAt(now.minusDays(12))
                .submittedAt(now.minusDays(13))
                .likesCount(78).dislikesCount(2).commentsCount(0).viewsCount(645)
                .emailSent(true)
                .build();

        // ── Blog 6: AI in Dev ──
        BlogPost blog6 = BlogPost.builder()
                .title("How AI is Transforming Software Development in 2026")
                .slug("ai-transforming-software-development-2026")
                .excerpt(
                        "From code completion to automated testing — exploring how AI tools are reshaping the developer experience.")
                .contentHtml(
                        """
                                                        <h1>AI in Software Development</h1>
                                                        <p>Artificial intelligence is no longer a futuristic concept — it's a daily part of the modern developer's toolkit.</p>

                                                        <h2>AI-Powered Code Assistants</h2>
                                                        <p>Tools like <strong>GitHub Copilot</strong> and <strong>Gemini Code Assist</strong> can generate entire functions from natural language descriptions:</p>

                                                        <pre><code>// Ask: "Create a function that validates email format"
                                function isValidEmail(email) {
                                    return email.includes('@') &amp;&amp; email.includes('.');
                                }</code></pre>

                                                        <h2>Automated Code Review</h2>
                                                        <blockquote>
                                                            <p>AI can catch bugs, style violations, and security vulnerabilities that humans often miss during code reviews.</p>
                                                        </blockquote>

                                                        <h2>The Balance</h2>
                                                        <p>While AI accelerates development, <em>understanding fundamentals</em> remains crucial. AI is a <u>co-pilot</u>, not an auto-pilot.</p>

                                                        <h3>Key Statistics</h3>
                                                        <ol>
                                                            <li>55% of developers use AI tools daily</li>
                                                            <li>40% faster feature delivery with AI assistance</li>
                                                            <li>30% reduction in production bugs</li>
                                                        </ol>
                                                        """)
                .featuredImageUrl("https://images.unsplash.com/photo-1677442136019-21780ecad995?w=800")
                .tags(Arrays.asList("ai", "machine-learning", "development", "future"))
                .authorName("Lisa Wang")
                .authorEmail("lisa.wang@example.com")
                .authorMobile("+1234567895")
                .status(BlogStatus.PUBLISHED)
                .year(now.getYear()).month(now.getMonthValue())
                .publishedAt(now.minusDays(1))
                .submittedAt(now.minusDays(2))
                .likesCount(205).dislikesCount(12).commentsCount(0).viewsCount(1240)
                .emailSent(true)
                .build();

        // ── Blog 7: MongoDB Aggregations ──
        BlogPost blog7 = BlogPost.builder()
                .title("MongoDB Aggregation Pipeline: A Practical Guide")
                .slug("mongodb-aggregation-pipeline-guide")
                .excerpt(
                        "Master MongoDB's powerful aggregation framework with real-world examples for data analysis and reporting.")
                .contentHtml(
                        """
                                                        <h2>Why Aggregation Pipelines?</h2>
                                                        <p>MongoDB's aggregation pipeline processes data through a sequence of <strong>stages</strong>, each transforming the documents.</p>

                                                        <h2>Common Stages</h2>
                                                        <pre><code>db.blogs.aggregate([
                                    { $match: { status: "PUBLISHED" } },
                                    { $group: {
                                        _id: { year: "$year", month: "$month" },
                                        count: { $sum: 1 },
                                        totalViews: { $sum: "$viewsCount" }
                                    }},
                                    { $sort: { "_id.year": -1, "_id.month": -1 } },
                                    { $limit: 12 }
                                ])</code></pre>

                                                        <blockquote><p>Think of aggregation stages as Unix pipes — each stage transforms data and passes it to the next.</p></blockquote>

                                                        <h3>$lookup — Joining Collections</h3>
                                                        <pre><code>{ $lookup: {
                                    from: "comments",
                                    localField: "_id",
                                    foreignField: "blogId",
                                    as: "comments"
                                }}</code></pre>

                                                        <h2>Performance Tips</h2>
                                                        <ul>
                                                            <li>Always <code>$match</code> early to reduce documents</li>
                                                            <li>Create indexes on fields used in <code>$match</code> and <code>$sort</code></li>
                                                            <li>Use <code>$project</code> to limit fields passing through</li>
                                                        </ul>
                                                        """)
                .featuredImageUrl("https://images.unsplash.com/photo-1558494949-ef010cbdcc31?w=800")
                .tags(Arrays.asList("mongodb", "database", "aggregation", "backend"))
                .authorName("Priya Sharma")
                .authorEmail("priya.sharma@example.com")
                .authorMobile("+1234567896")
                .status(BlogStatus.PUBLISHED)
                .year(now.getYear()).month(now.getMonthValue())
                .publishedAt(now.minusDays(15))
                .submittedAt(now.minusDays(16))
                .likesCount(45).dislikesCount(1).commentsCount(0).viewsCount(310)
                .emailSent(true)
                .build();

        // ── Blog 8: TypeScript Tips ──
        BlogPost blog8 = BlogPost.builder()
                .title("10 TypeScript Tricks Every Developer Should Know")
                .slug("10-typescript-tricks")
                .excerpt(
                        "Level up your TypeScript skills with these lesser-known but incredibly useful type system features.")
                .contentHtml(
                        """
                                                        <h1>TypeScript Tricks</h1>

                                                        <h2>1. Template Literal Types</h2>
                                                        <pre><code>type EventName = `on${Capitalize&lt;string&gt;}`;
                                // "onClick", "onChange", "onSubmit" etc.</code></pre>

                                                        <h2>2. Discriminated Unions</h2>
                                                        <pre><code>type Result&lt;T&gt; =
                                    | { success: true; data: T }
                                    | { success: false; error: string };

                                function handle(result: Result&lt;User&gt;) {
                                    if (result.success) {
                                        console.log(result.data); // TypeScript knows this is User
                                    } else {
                                        console.error(result.error); // TypeScript knows this is string
                                    }
                                }</code></pre>

                                                        <h2>3. Const Assertions</h2>
                                                        <pre><code>const COLORS = ['red', 'green', 'blue'] as const;
                                type Color = typeof COLORS[number]; // 'red' | 'green' | 'blue'</code></pre>

                                                        <blockquote><p>TypeScript's type system is Turing-complete — you can express almost any constraint at the type level.</p></blockquote>

                                                        <h3>More Tricks</h3>
                                                        <ol>
                                                            <li>Mapped types for transforming shapes</li>
                                                            <li>Conditional types for type-level logic</li>
                                                            <li>Infer keyword for extracting types</li>
                                                            <li>Branded types for nominal typing</li>
                                                        </ol>
                                                        """)
                .featuredImageUrl("https://images.unsplash.com/photo-1555066931-4365d14bab8c?w=800")
                .tags(Arrays.asList("typescript", "javascript", "tips", "frontend"))
                .authorName("Alex Thompson")
                .authorEmail("alex.thompson@example.com")
                .authorMobile("+1234567897")
                .status(BlogStatus.PUBLISHED)
                .year(now.getYear()).month(now.getMonthValue())
                .publishedAt(now.minusDays(20))
                .submittedAt(now.minusDays(21))
                .likesCount(112).dislikesCount(6).commentsCount(0).viewsCount(780)
                .emailSent(true)
                .build();

        // ── Blog 9: Git Workflows ──
        BlogPost blog9 = BlogPost.builder()
                .title("Git Branching Strategies for Teams")
                .slug("git-branching-strategies-teams")
                .excerpt(
                        "Compare GitFlow, GitHub Flow, and Trunk-Based Development to find the right workflow for your team.")
                .contentHtml(
                        """
                                                      <h2>GitFlow</h2>
                                                      <p>Best for projects with scheduled releases:</p>
                                                      <pre><code>main ──────────────────────────────
                                \\                               /
                                 develop ───────────────────────
                                   \\           \\       /       /
                                    feature/A   feature/B
                                                  \\
                                                   release/1.0</code></pre>

                                                      <h2>GitHub Flow</h2>
                                                      <p>Simpler — great for continuous deployment:</p>
                                                      <pre><code>main ──────────────────────────────
                                \\       /   \\       /
                                 feature     hotfix</code></pre>

                                                      <h2>Trunk-Based Development</h2>
                                                      <blockquote><p>Everyone commits to main. Use feature flags instead of long-lived branches.</p></blockquote>

                                                      <h3>Which Should You Choose?</h3>
                                                      <ul>
                                                          <li><strong>Small team, CD:</strong> GitHub Flow or Trunk-Based</li>
                                                          <li><strong>Large team, scheduled releases:</strong> GitFlow</li>
                                                          <li><strong>Startups:</strong> GitHub Flow</li>
                                                      </ul>
                                                      """)
                .featuredImageUrl("https://images.unsplash.com/photo-1556075798-4825dfaaf498?w=800")
                .tags(Arrays.asList("git", "workflow", "devops", "team"))
                .authorName("Carlos Silva")
                .authorEmail("carlos.silva@example.com")
                .authorMobile("+1234567898")
                .status(BlogStatus.PUBLISHED)
                .year(now.minusMonths(1).getYear()).month(now.minusMonths(1).getMonthValue())
                .publishedAt(now.minusDays(40))
                .submittedAt(now.minusDays(41))
                .likesCount(34).dislikesCount(2).commentsCount(0).viewsCount(256)
                .emailSent(true)
                .build();

        // ── Blog 10: Security ──
        BlogPost blog10 = BlogPost.builder()
                .title("Web Security Essentials: OWASP Top 10 Explained")
                .slug("web-security-owasp-top-10")
                .excerpt("Understand the most critical web application security risks and how to defend against them.")
                .contentHtml(
                        """
                                                        <h1>OWASP Top 10 — 2025 Edition</h1>
                                                        <p>Every developer should understand these <strong>critical security risks</strong>.</p>

                                                        <h2>1. Injection Attacks</h2>
                                                        <pre><code>// ❌ Vulnerable to SQL injection
                                String query = "SELECT * FROM users WHERE email = '" + email + "'";

                                // ✅ Use parameterized queries
                                @Query("SELECT u FROM User u WHERE u.email = :email")
                                User findByEmail(@Param("email") String email);</code></pre>

                                                        <h2>2. Cross-Site Scripting (XSS)</h2>
                                                        <p>Always <u>sanitize user input</u> before rendering:</p>
                                                        <pre><code>// ❌ Dangerous
                                element.innerHTML = userInput;

                                // ✅ Safe
                                element.textContent = userInput;</code></pre>

                                                        <blockquote><p>Security is not a feature — it's a requirement. Build it into every layer of your application.</p></blockquote>

                                                        <h3>Quick Checklist</h3>
                                                        <ol>
                                                            <li>Always validate and sanitize input</li>
                                                            <li>Use HTTPS everywhere</li>
                                                            <li>Implement CSRF protection</li>
                                                            <li>Hash passwords with bcrypt/argon2</li>
                                                            <li>Set security headers (CSP, HSTS)</li>
                                                        </ol>
                                                        """)
                .featuredImageUrl("https://images.unsplash.com/photo-1550751827-4bd374c3f58b?w=800")
                .tags(Arrays.asList("security", "owasp", "web-development", "backend"))
                .authorName("Natasha Romanov")
                .authorEmail("natasha.r@example.com")
                .authorMobile("+1234567899")
                .status(BlogStatus.PUBLISHED)
                .year(now.getYear()).month(now.getMonthValue())
                .publishedAt(now.minusDays(7))
                .submittedAt(now.minusDays(8))
                .likesCount(92).dislikesCount(3).commentsCount(0).viewsCount(567)
                .emailSent(true)
                .build();

        // ── Blog 11: Spring Security ──
        BlogPost blog11 = BlogPost.builder()
                .title("Spring Security 6: JWT Authentication from Scratch")
                .slug("spring-security-6-jwt-authentication")
                .excerpt(
                        "Build a complete JWT-based authentication system with Spring Security 6, including refresh tokens and role-based access.")
                .contentHtml(
                        """
                                                        <h2>JWT Authentication Flow</h2>
                                                        <pre><code>Client → POST /auth/login (credentials)
                                  ↓
                                Server → Returns { accessToken, refreshToken }
                                  ↓
                                Client → GET /api/blogs (Authorization: Bearer {token})
                                  ↓
                                Server → JwtFilter validates token → returns data</code></pre>

                                                        <h2>Security Configuration</h2>
                                                        <pre><code>@Bean
                                SecurityFilterChain filterChain(HttpSecurity http) {
                                    return http
                                        .csrf(csrf -> csrf.disable())
                                        .sessionManagement(sm -> sm.sessionCreationPolicy(STATELESS))
                                        .authorizeHttpRequests(auth -> auth
                                            .requestMatchers("/auth/**").permitAll()
                                            .requestMatchers("/admin/**").hasRole("ADMIN")
                                            .anyRequest().authenticated()
                                        )
                                        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                                        .build();
                                }</code></pre>

                                                        <blockquote><p>Never store JWTs in localStorage. Use httpOnly cookies for better XSS protection.</p></blockquote>
                                                        """)
                .featuredImageUrl("https://images.unsplash.com/photo-1555066931-4365d14bab8c?w=800")
                .tags(Arrays.asList("spring-security", "jwt", "authentication", "java"))
                .authorName("Sarah Johnson")
                .authorEmail("sarah.johnson@example.com")
                .authorMobile("+1234567890")
                .status(BlogStatus.PUBLISHED)
                .year(now.getYear()).month(now.getMonthValue())
                .publishedAt(now.minusDays(14))
                .submittedAt(now.minusDays(15))
                .likesCount(68).dislikesCount(4).commentsCount(0).viewsCount(412)
                .emailSent(true)
                .build();

        // ── Blog 12: Performance ──
        BlogPost blog12 = BlogPost.builder()
                .title("Frontend Performance Optimization Checklist")
                .slug("frontend-performance-optimization-checklist")
                .excerpt(
                        "A practical checklist to make your web application blazing fast — from loading to runtime performance.")
                .contentHtml(
                        """
                                                        <h1>Performance Optimization Checklist</h1>

                                                        <h2>Loading Performance</h2>
                                                        <ul>
                                                            <li>Enable <strong>gzip/brotli</strong> compression</li>
                                                            <li>Use <code>loading="lazy"</code> for images below the fold</li>
                                                            <li>Split code with dynamic <code>import()</code></li>
                                                            <li>Preload critical resources</li>
                                                        </ul>

                                                        <h2>Runtime Performance</h2>
                                                        <pre><code>// Debounce expensive handlers
                                const handleSearch = debounce((query) => {
                                    fetchResults(query);
                                }, 300);

                                // Virtualize long lists
                                &lt;VirtualList items={items} itemHeight={60} /&gt;</code></pre>

                                                        <h2>Core Web Vitals</h2>
                                                        <ol>
                                                            <li><strong>LCP</strong> (Largest Contentful Paint) &lt; 2.5s</li>
                                                            <li><strong>FID</strong> (First Input Delay) &lt; 100ms</li>
                                                            <li><strong>CLS</strong> (Cumulative Layout Shift) &lt; 0.1</li>
                                                        </ol>

                                                        <blockquote><p>If you can't measure it, you can't improve it. Use Lighthouse and Web Vitals API.</p></blockquote>
                                                        """)
                .featuredImageUrl("https://images.unsplash.com/photo-1460925895917-afdab827c52f?w=800")
                .tags(Arrays.asList("performance", "frontend", "optimization", "web-vitals"))
                .authorName("Emily Rodriguez")
                .authorEmail("emily.rodriguez@example.com")
                .authorMobile("+1234567891")
                .status(BlogStatus.PUBLISHED)
                .year(now.minusMonths(1).getYear()).month(now.minusMonths(1).getMonthValue())
                .publishedAt(now.minusDays(30))
                .submittedAt(now.minusDays(31))
                .likesCount(54).dislikesCount(1).commentsCount(0).viewsCount(389)
                .emailSent(true)
                .build();

        // ── Blog 13 (PENDING) ──
        BlogPost blog13 = BlogPost.builder()
                .title("Microservices vs Monoliths: The Definitive Comparison")
                .slug("microservices-vs-monoliths-comparison")
                .excerpt(
                        "A balanced comparison of microservices and monolithic architectures to help you make the right choice for your project.")
                .contentHtml("""
                        <h2>The Monolith</h2>
                        <p>A single, unified application. <strong>Start here</strong> for most projects.</p>
                        <h3>Pros</h3>
                        <ul><li>Simple development</li><li>Easy debugging</li><li>Single deployment</li></ul>
                        <h3>Cons</h3>
                        <ul><li>Scaling challenges</li><li>Long build times at scale</li></ul>

                        <h2>Microservices</h2>
                        <p>Decomposed into small, independent services.</p>
                        <blockquote><p>Don't start with microservices. Earn the complexity.</p></blockquote>
                        """)
                .featuredImageUrl("https://images.unsplash.com/photo-1618401471353-b98afee0b2eb?w=800")
                .tags(Arrays.asList("microservices", "architecture", "scalability"))
                .authorName("Lisa Wang")
                .authorEmail("lisa.wang@example.com")
                .authorMobile("+1234567895")
                .status(BlogStatus.PENDING)
                .submittedAt(now.minusHours(2))
                .emailSent(false)
                .build();

        // ── Blog 14 (PENDING) ──
        BlogPost blog14 = BlogPost.builder()
                .title("Building a Real-Time Chat Application with WebSockets")
                .slug("real-time-chat-websockets")
                .excerpt(
                        "Step-by-step tutorial on building a real-time chat app using Spring Boot WebSockets and React.")
                .contentHtml("""
                                                <h1>Real-Time Chat with WebSockets</h1>
                                                <h2>Server Side (Spring Boot)</h2>
                                                <pre><code>@Configuration
                        @EnableWebSocketMessageBroker
                        public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
                            @Override
                            public void registerStompEndpoints(StompEndpointRegistry registry) {
                                registry.addEndpoint("/ws").setAllowedOrigins("*").withSockJS();
                            }
                        }</code></pre>

                                                <h2>Client Side (React)</h2>
                                                <pre><code>const client = new Client({
                            brokerURL: 'ws://localhost:8080/ws',
                            onConnect: () => {
                                client.subscribe('/topic/messages', (msg) => {
                                    setMessages(prev => [...prev, JSON.parse(msg.body)]);
                                });
                            }
                        });</code></pre>
                                                """)
                .featuredImageUrl("https://images.unsplash.com/photo-1611606063065-ee7946f0787a?w=800")
                .tags(Arrays.asList("websockets", "real-time", "chat", "spring-boot"))
                .authorName("Raj Patel")
                .authorEmail("raj.patel@example.com")
                .authorMobile("+1234567800")
                .status(BlogStatus.PENDING)
                .submittedAt(now.minusHours(5))
                .emailSent(false)
                .build();

        // ── Blog 15 (PENDING) ──
        BlogPost blog15 = BlogPost.builder()
                .title("Kubernetes for Java Developers: A Beginner's Guide")
                .slug("kubernetes-java-developers-guide")
                .excerpt("Deploy and scale your Spring Boot applications on Kubernetes with zero prior K8s experience.")
                .contentHtml(
                        """
                                                        <h2>What is Kubernetes?</h2>
                                                        <p>Kubernetes (K8s) automates deploying, scaling, and managing containerized applications.</p>

                                                        <h2>Your First Deployment</h2>
                                                        <pre><code>apiVersion: apps/v1
                                kind: Deployment
                                metadata:
                                  name: blog-app
                                spec:
                                  replicas: 3
                                  selector:
                                    matchLabels:
                                      app: blog-app
                                  template:
                                    spec:
                                      containers:
                                        - name: blog-app
                                          image: blog-app:latest
                                          ports:
                                            - containerPort: 8080</code></pre>

                                                        <blockquote><p>Kubernetes is like having a very organized robot managing your server room.</p></blockquote>
                                                        """)
                .featuredImageUrl("https://images.unsplash.com/photo-1667372393119-3d4c48d07fc9?w=800")
                .tags(Arrays.asList("kubernetes", "devops", "java", "cloud"))
                .authorName("Anna Schmidt")
                .authorEmail("anna.schmidt@example.com")
                .authorMobile("+1234567801")
                .status(BlogStatus.PENDING)
                .submittedAt(now.minusHours(8))
                .emailSent(false)
                .build();

        // ── Blog 16 (DRAFT) ──
        BlogPost blog16 = BlogPost.builder()
                .title("Testing Strategies: Unit, Integration, and E2E")
                .slug("testing-strategies-unit-integration-e2e")
                .excerpt("A comprehensive guide to testing at every level of your application stack.")
                .contentHtml("""
                                                <h2>The Testing Pyramid</h2>
                                                <p>Write MANY unit tests, SOME integration tests, FEW E2E tests.</p>
                                                <pre><code>@Test
                        void shouldCreateBlog() {
                            BlogPost blog = BlogPost.builder()
                                .title("Test Blog")
                                .slug("test-blog")
                                .build();

                            assertNotNull(blog);
                            assertEquals("Test Blog", blog.getTitle());
                        }</code></pre>
                                                """)
                .tags(Arrays.asList("testing", "junit", "best-practices"))
                .authorName("Michael Chen")
                .authorEmail("michael.chen@example.com")
                .authorMobile("+1234567892")
                .status(BlogStatus.DRAFT)
                .emailSent(false)
                .build();

        // ── Blog 17 (REJECTED) ──
        BlogPost blog17 = BlogPost.builder()
                .title("Why PHP is Still Relevant in 2026")
                .slug("php-still-relevant-2026")
                .excerpt("A controversial take on PHP's place in the modern web development landscape.")
                .contentHtml(
                        """
                                <h2>PHP Powers 77% of the Web</h2>
                                <p>Love it or hate it, PHP isn't going anywhere. WordPress, Laravel, and Symfony continue to thrive.</p>
                                <blockquote><p>PHP 8.3 is actually a beautiful language. Give it another chance.</p></blockquote>
                                """)
                .tags(Arrays.asList("php", "web-development", "opinion"))
                .authorName("Web Dev Enthusiast")
                .authorEmail("webdev@example.com")
                .authorMobile("+1234567802")
                .status(BlogStatus.REJECTED)
                .rejectionReason("Article needs more depth and code examples. Please expand the technical analysis.")
                .submittedAt(now.minusDays(3))
                .emailSent(false)
                .build();

        return Arrays.asList(blog1, blog2, blog3, blog4, blog5, blog6,
                blog7, blog8, blog9, blog10, blog11, blog12,
                blog13, blog14, blog15, blog16, blog17);
    }

    private List<BlogComment> createSampleComments(String blogId) {
        BlogComment comment1 = BlogComment.builder()
                .blogId(blogId)
                .name("Alex Thompson")
                .email("alex.t@example.com")
                .commentText(
                        "This is an amazing deep-dive into modern Java! The section on virtual threads was particularly eye-opening.")
                .status(CommentStatus.VISIBLE)
                .build();

        BlogComment comment2 = BlogComment.builder()
                .blogId(blogId)
                .name("Priya Sharma")
                .email("priya.s@example.com")
                .commentText(
                        "Thanks for the code examples — I was able to refactor my project using records and sealed classes right away!")
                .status(CommentStatus.VISIBLE)
                .build();

        BlogComment comment3 = BlogComment.builder()
                .blogId(blogId)
                .name("Carlos Silva")
                .email("carlos.silva@example.com")
                .commentText(
                        "Could you write a follow-up about Project Panama and the Foreign Function API? That would be super useful.")
                .status(CommentStatus.VISIBLE)
                .build();

        return Arrays.asList(comment1, comment2, comment3);
    }

    private List<Subscriber> createSampleSubscribers() {
        Subscriber sub1 = Subscriber.builder()
                .email("reader1@example.com")
                .name("John Reader")
                .status(SubscriberStatus.ACTIVE)
                .verified(true)
                .build();

        Subscriber sub2 = Subscriber.builder()
                .email("reader2@example.com")
                .name("Jane Subscriber")
                .status(SubscriberStatus.ACTIVE)
                .verified(true)
                .build();

        Subscriber sub3 = Subscriber.builder()
                .email("tech.enthusiast@example.com")
                .name("Tech Enthusiast")
                .status(SubscriberStatus.ACTIVE)
                .verified(true)
                .build();

        Subscriber sub4 = Subscriber.builder()
                .email("dev.weekly@example.com")
                .name("Dev Weekly")
                .status(SubscriberStatus.ACTIVE)
                .verified(true)
                .build();

        Subscriber sub5 = Subscriber.builder()
                .email("former.reader@example.com")
                .name("Former Reader")
                .status(SubscriberStatus.UNSUBSCRIBED)
                .verified(true)
                .build();

        return Arrays.asList(sub1, sub2, sub3, sub4, sub5);
    }

    // =========== Section / Subsection Seeding ===========

    private void seedSections() {
        if (sectionRepository.count() > 0) {
            log.info("Sections already seeded — skipping section initializer");
            return;
        }

        log.info("Seeding initial sections and subsections...");

        // 1. Literature
        Section literature = saveSection("Literature", "literature", 1);
        saveSubsections(literature.getId(), List.of(
                new String[] { "Stories", "stories", "Short stories, epics, and narrative fiction" },
                new String[] { "Poems", "poems", "Poetry in all forms" },
                new String[] { "Screenwriting", "screenwriting", "Scripts and screenplays" },
                new String[] { "Book Reviews", "book-reviews", "Reviews and literary criticism" },
                new String[] { "Essays", "essays", "Literary and personal essays" }));

        // 2. Lifestyle
        Section lifestyle = saveSection("Lifestyle", "lifestyle", 2);
        saveSubsections(lifestyle.getId(), List.of(
                new String[] { "Style", "style", "Fashion, beauty, and personal style" },
                new String[] { "Decor", "decor", "Home décor and interior design" },
                new String[] { "Travel", "travel", "Travel guides and journeys" },
                new String[] { "Food", "food", "Recipes, reviews, and food culture" },
                new String[] { "Wellness", "wellness", "Health, fitness, and mental well-being" }));

        // 3. Opinion
        Section opinion = saveSection("Opinion", "opinion", 3);
        saveSubsections(opinion.getId(), List.of(
                new String[] { "Editorials", "editorials", "Staff and guest editorials" },
                new String[] { "Debates", "debates", "Two-sided perspectives and analysis" },
                new String[] { "Letters", "letters", "Letters to the editor and open letters" },
                new String[] { "Commentary", "commentary", "Current affairs commentary" }));

        // 4. Knowledge & Learning
        Section knowledge = saveSection("Knowledge & Learning", "knowledge-learning", 4);
        saveSubsections(knowledge.getId(), List.of(
                new String[] { "Science", "science", "Science and technology" },
                new String[] { "History", "history", "Historical deep dives" },
                new String[] { "How-To", "how-to", "Tutorials and step-by-step guides" },
                new String[] { "Tech", "tech", "Technology trends and reviews" },
                new String[] { "Finance", "finance", "Personal finance and economics" }));

        // 5. Personal
        Section personal = saveSection("Personal", "personal", 5);
        saveSubsections(personal.getId(), List.of(
                new String[] { "Journal", "journal", "Personal diaries and reflections" },
                new String[] { "Memoir", "memoir", "Life stories and memoirs" },
                new String[] { "Confessions", "confessions", "Anonymous personal stories" },
                new String[] { "Mindfulness", "mindfulness", "Meditation and mindful living" }));

        log.info("Successfully seeded {} sections", sectionRepository.count());
    }

    private Section saveSection(String name, String slug, int sortOrder) {
        Section section = Section.builder()
                .name(name)
                .slug(slug)
                .sortOrder(sortOrder)
                .build();
        return sectionRepository.save(section);
    }

    private void saveSubsections(String sectionId, List<String[]> data) {
        int order = 1;
        for (String[] sub : data) {
            Subsection subsection = Subsection.builder()
                    .sectionId(sectionId)
                    .name(sub[0])
                    .slug(sub[1])
                    .description(sub[2])
                    .sortOrder(order++)
                    .build();
            subsectionRepository.save(subsection);
        }
    }
}
