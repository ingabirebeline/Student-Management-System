CREATE TABLE IF NOT EXISTS students (
                                        id SERIAL PRIMARY KEY,
                                        full_name VARCHAR(150) NOT NULL,
    age INTEGER CHECK (age > 18),
    email VARCHAR(150) UNIQUE NOT NULL
    );