CREATE TABLE invoice (
                         invoice_id INT PRIMARY KEY,
                         customer_id INT,
                         delivery_method NVARCHAR(100),
                         issue_date DATE,
                         order_status NVARCHAR(50),
                         payment_method NVARCHAR(50),
                         receiver_address NVARCHAR(255),
                         receiver_name NVARCHAR(100),
                         receiver_number NVARCHAR(20),
                         total FLOAT
);

CREATE TABLE invoice_detail (
                                detail_id INT PRIMARY KEY,
                                product_id NVARCHAR(100),
                                quantity INT,
                                invoice_id INT FOREIGN KEY REFERENCES invoice(invoice_id)
);

INSERT INTO invoice VALUES
                        (1, 4, 'Standard Delivery', '2025-05-08', 'Processing', 'Cash on Delivery', 'Gò Vấp, Hồ Chí Minh', 'NHI PHAM THI THUY', '222222222', 287.5),
                        (2, 4, 'Standard Delivery', '2025-05-08', 'Canceled', 'Cash on Delivery', 'Gò Vấp, Hồ Chí Minh', 'NHI PHAM THI THUY', '222222222', 196),
                        (3, 4, 'Collect in store', '2025-05-08', 'Processing', 'Cash on Delivery', 'Gò Vấp, Hồ Chí Minh', 'NHI PHAM THI THUY', '222222222', 6602.5),
                        (4, 5, 'Standard Delivery', '2025-05-08', 'Processing', 'Cash on Delivery', 'Gò Vấp, Hồ Chí Minh', 'NHI PHAM THI THUY', '222222222', 87.5),
                        (5, 5, 'Collect in store', '2025-05-08', 'Delivered', 'Cash on Delivery', 'Gò Vấp, Hồ Chí Minh', 'Ly Huỳnh', '222222222', 490);

INSERT INTO invoice_detail VALUES
                               (1, '679d05899bb071bf2179414', 2, 1),
                               (2, '679d72f0a1f6426dfa3afa2', 1, 1),
                               (3, '679d82e0a1f6426dfa3afd6', 1, 2),
                               (4, '679d4a20a1f6426dfa3afd8', 49, 3),
                               (5, '679d87c0a1f6426dfa3aff5', 6, 3),
                               (6, '679d72f0a1f6426dfa3afa2', 1, 4),
                               (7, '679d87c0a1f6426dfa3aff5', 3, 5),
                               (8, '679d82e0a1f6426dfa3afd6', 1, 5);
