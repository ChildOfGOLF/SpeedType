-- для инициализации базы данных SpeedType

INSERT INTO users (username, password, role) VALUES
('admin', '$2a$10$0EgUt/2JcMn5OAkLgMs/kuHpIVLZ5l6m00UmMEEZlgrqbAZTmxr.a', 'ADMIN'),
('testuser', '$2a$10$0EgUt/2JcMn5OAkLgMs/kuHpIVLZ5l6m00UmMEEZlgrqbAZTmxr.a', 'USER');

INSERT INTO texts (content, difficulty, language) VALUES
('The quick brown fox jumps over the lazy dog. This is a simple test.', 'easy', 'en'),
('Programming requires logical thinking and attention to detail.', 'easy', 'en'),
('Learning to type efficiently improves productivity and reduces fatigue.', 'easy', 'en'),
('Advanced algorithms and data structures are fundamental concepts in computer science.', 'medium', 'en'),
('Implementing efficient sorting algorithms such as quicksort and mergesort requires understanding of recursion.', 'medium', 'en'),
('Object-oriented programming principles include encapsulation, inheritance, and polymorphism.', 'medium', 'en'),
('Quantum computing represents a paradigm shift in computational methodology, utilizing quantum mechanical phenomena.', 'hard', 'en'),
('Cryptographic protocols ensure secure communication through mathematical complexity and computational infeasibility.', 'hard', 'en'),
('Machine learning algorithms require extensive training on large datasets to achieve optimal performance metrics.', 'hard', 'en'),

('Быстрая коричневая лиса прыгает через ленивую собаку. Это простой тест печати.', 'easy', 'ru'),
('Программирование требует логического мышления и внимания к деталям.', 'easy', 'ru'),
('Изучение эффективной печати повышает продуктивность и снижает усталость.', 'easy', 'ru'),
('Продвинутые алгоритмы и структуры данных являются фундаментальными концепциями информатики.', 'medium', 'ru'),
('Реализация эффективных алгоритмов сортировки требует понимания принципов рекурсии.', 'medium', 'ru'),
('Принципы объектно-ориентированного программирования включают инкапсуляцию, наследование и полиморфизм.', 'medium', 'ru'),
('Квантовые вычисления представляют смену парадигмы в вычислительной методологии, используя квантовые механические явления.', 'hard', 'ru'),
('Криптографические протоколы обеспечивают безопасную связь через математическую сложность и вычислительную невозможность.', 'hard', 'ru'),
('Алгоритмы машинного обучения требуют обширного обучения на больших наборах данных для достижения оптимальных показателей производительности.', 'hard', 'ru');
