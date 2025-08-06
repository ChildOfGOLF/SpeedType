
INSERT INTO users (username, password, role) VALUES
('admin', '$2a$10$0EgUt/2JcMn5OAkLgMs/kuHpIVLZ5l6m00UmMEEZlgrqbAZTmxr.a', 'ADMIN'),
('testuser', '$2a$10$0EgUt/2JcMn5OAkLgMs/kuHpIVLZ5l6m00UmMEEZlgrqbAZTmxr.a', 'USER');

INSERT INTO texts (content, difficulty) VALUES
('The quick brown fox jumps over the lazy dog. This is a simple test.', 'easy'),
('Programming requires logical thinking and attention to detail.', 'easy'),
('Learning to type efficiently improves productivity and reduces fatigue.', 'easy'),
('Advanced algorithms and data structures are fundamental concepts in computer science.', 'medium'),
('Implementing efficient sorting algorithms such as quicksort and mergesort requires understanding of recursion.', 'medium'),
('Object-oriented programming principles include encapsulation, inheritance, and polymorphism.', 'medium'),
('Quantum computing represents a paradigm shift in computational methodology, utilizing quantum mechanical phenomena.', 'hard'),
('Cryptographic protocols ensure secure communication through mathematical complexity and computational infeasibility.', 'hard'),
('Machine learning algorithms require extensive training on large datasets to achieve optimal performance metrics.', 'hard');

SELECT 'users' as table_name, count(*) as count FROM users
UNION ALL
SELECT 'texts' as table_name, count(*) as count FROM texts;

