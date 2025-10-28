-- CLEAN UP: DROP ALL TABLES IF THEY EXIST TO AVOID CONFLICTS
DROP TABLE IF EXISTS
    public.service_record_details,
    public.service_records,
    public.bookings,
    public.user_roles,
    public.maintenance_schedule,
    public.car_models,
    public.maintenance_item,
    public.app_user,
    public.vehicles
    CASCADE;

-- =====================================================================
-- app_user (STORES USER INFORMATION)
-- =====================================================================
CREATE TABLE app_user (
                          id BIGSERIAL PRIMARY KEY,
                          sub VARCHAR(255) UNIQUE,
                          email VARCHAR(255) UNIQUE NOT NULL,
                          full_name VARCHAR(255),
                          active BOOLEAN NOT NULL DEFAULT TRUE,

                          created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
                          created_by VARCHAR(255) NOT NULL,
                          updated_at TIMESTAMP WITHOUT TIME ZONE,
                          updated_by VARCHAR(255)
);

COMMENT ON TABLE app_user IS 'Stores basic information about users.';
COMMENT ON COLUMN app_user.sub IS 'Unique identifier from the OAuth2 provider (Google Subject).';
COMMENT ON COLUMN app_user.email IS 'User''s email, used for identification.';
COMMENT ON COLUMN app_user.full_name IS 'User''s full name (from Google).';
COMMENT ON COLUMN app_user.active IS 'Account status (true = active, false = soft-deleted/locked).';


-- =====================================================================
-- vehicles (STORES THE USER''S VEHICLE GARAGE)
-- =====================================================================
CREATE TABLE vehicles (
                          id BIGSERIAL PRIMARY KEY,
                          owner_id BIGINT NOT NULL,
                          license_plate VARCHAR(255) NOT NULL UNIQUE,
                          car_model VARCHAR(255) NOT NULL,
                          vin_number VARCHAR(255),
                          active BOOLEAN NOT NULL DEFAULT TRUE,
                          created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
                          created_by VARCHAR(255) NOT NULL,
                          updated_at TIMESTAMP WITHOUT TIME ZONE,
                          updated_by VARCHAR(255),
                          CONSTRAINT fk_vehicles_owner FOREIGN KEY (owner_id) REFERENCES app_user(id)
);
COMMENT ON TABLE vehicles IS 'Stores information about vehicles owned by users.';


-- =====================================================================
-- user_roles (STORES USER ROLES - Many-to-Many relationship)
-- =====================================================================
CREATE TABLE user_roles (
                            user_id BIGINT NOT NULL,
                            role VARCHAR(255) NOT NULL,
                            PRIMARY KEY (user_id, role),
                            CONSTRAINT fk_user_roles_app_user FOREIGN KEY (user_id) REFERENCES app_user(id)
);

COMMENT ON TABLE user_roles IS 'Stores the roles assigned to each user.';
COMMENT ON COLUMN user_roles.user_id IS 'Foreign key pointing to the user''s ID in the app_user table.';


-- =====================================================================
-- bookings (STORES SERVICE APPOINTMENT INFORMATION)
-- =====================================================================
CREATE TABLE bookings (
                          id BIGSERIAL PRIMARY KEY,
                          user_id BIGINT NOT NULL,
                          technician_id BIGINT,
                          customer_phone_number VARCHAR(255) NOT NULL,
                          license_plate VARCHAR(255) NOT NULL,
                          car_model VARCHAR(255),
                          vin_number VARCHAR(255),
                          service_center VARCHAR(255) NOT NULL,
                          appointment_date_time TIMESTAMP WITHOUT TIME ZONE NOT NULL,
                          service_advisor VARCHAR(255),
                          notes TEXT,
                          status VARCHAR(255) NOT NULL,
                          created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
                          created_by VARCHAR(255) NOT NULL,
                          updated_at TIMESTAMP WITHOUT TIME ZONE,
                          updated_by VARCHAR(255),
                          CONSTRAINT fk_bookings_app_user FOREIGN KEY (user_id) REFERENCES app_user(id),
                          CONSTRAINT fk_bookings_technician FOREIGN KEY (technician_id) REFERENCES app_user(id)
);

COMMENT ON TABLE bookings IS 'Stores information about vehicle service appointments.';
COMMENT ON COLUMN bookings.user_id IS 'Foreign key pointing to the user who created the appointment.';


-- =====================================================================
-- service_records (STORES SERVICE HISTORY)
-- =====================================================================
CREATE TABLE service_records (
                                 id BIGSERIAL PRIMARY KEY,
                                 booking_id BIGINT UNIQUE,
                                 license_plate VARCHAR(255) NOT NULL,
                                 kilometer_reading INTEGER NOT NULL,
                                 service_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
                                 created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
                                 created_by VARCHAR(255) NOT NULL,
                                 CONSTRAINT fk_records_bookings FOREIGN KEY (booking_id) REFERENCES bookings(id)
);

COMMENT ON TABLE service_records IS 'Service orders, storing the history of completed maintenance.';


-- =====================================================================
-- service_record_details (STORES DETAILS OF COMPLETED ITEMS)
-- =====================================================================
CREATE TABLE service_record_details (
                                        id BIGSERIAL PRIMARY KEY,
                                        service_record_id BIGINT NOT NULL,
                                        item_name VARCHAR(255) NOT NULL,
                                        action VARCHAR(255) NOT NULL,
                                        notes TEXT,
                                        price NUMERIC(12, 2) NOT NULL DEFAULT 0.00,
                                        CONSTRAINT fk_details_records FOREIGN KEY (service_record_id) REFERENCES service_records(id)
);

COMMENT ON TABLE service_record_details IS 'Details of each item performed during a maintenance session.';


-- =====================================================================
-- car_models
-- =====================================================================
CREATE TABLE car_models (
                            id BIGSERIAL PRIMARY KEY,
                            name VARCHAR(255) NOT NULL UNIQUE,
                            segment VARCHAR(255) NOT NULL
);


-- =====================================================================
-- maintenance_item (STORES MAINTENANCE ITEM CATALOG)
-- =====================================================================
CREATE TABLE maintenance_item (
                                  id BIGSERIAL PRIMARY KEY,
                                  name VARCHAR(255) NOT NULL UNIQUE,
                                  category VARCHAR(255) NOT NULL
);

COMMENT ON TABLE maintenance_item IS 'A catalog of all possible maintenance items.';


-- =====================================================================
-- maintenance_schedule (STORES THE DETAILED MAINTENANCE SCHEDULE)
-- =====================================================================
CREATE TABLE maintenance_schedule (
                                      id BIGSERIAL PRIMARY KEY,
                                      car_model_id BIGINT NOT NULL,
                                      item_id BIGINT NOT NULL,
                                      kilometer_mark INTEGER NOT NULL,
                                      action VARCHAR(255) NOT NULL,
                                      price NUMERIC(12, 2) DEFAULT 0.00,
                                      CONSTRAINT fk_schedule_car_model FOREIGN KEY (car_model_id) REFERENCES car_models(id),
                                      CONSTRAINT fk_schedule_item FOREIGN KEY (item_id) REFERENCES maintenance_item(id)
);

COMMENT ON TABLE maintenance_schedule IS 'Detailed schedule: which item, what action, at which km milestone.';


-- =====================================================================
-- SEED DATA FOR MAINTENANCE TABLES
-- =====================================================================

-- Insert data for car_models table
INSERT INTO car_models (id, name, segment) VALUES
                                               (1, 'VF 3',      'MINI_CAR'),
                                               (2, 'VF 5 Plus', 'A_SUV'),
                                               (3, 'VF 6',      'B_SUV'),
                                               (4, 'VF 7',      'C_SUV'),
                                               (5, 'VF e34',    'C_SUV'),
                                               (6, 'VF 8',      'D_SUV'),
                                               (7, 'VF 9',      'E_SUV');


-- Insert data for maintenance_item table
INSERT INTO maintenance_item (id, name, category) VALUES
                                                      (1, 'Cabin Air Filter', 'MAINTENANCE ITEMS'),
                                                      (2, 'Brake Fluid', 'MAINTENANCE ITEMS'),
                                                      (3, 'Air Conditioning System Service', 'MAINTENANCE ITEMS'),
                                                      (4, 'Key Fob Battery', 'MAINTENANCE ITEMS'),
                                                      (5, 'T-Box Battery', 'MAINTENANCE ITEMS'),
                                                      (6, 'Coolant for Battery/Electric Motor', 'MAINTENANCE ITEMS'),
                                                      (7, 'Tires (pressure, wear, rotation, and balancing)', 'GENERAL MAINTENANCE ITEMS'),
                                                      (8, 'Brake Pads and Discs', 'GENERAL MAINTENANCE ITEMS'),
                                                      (9, 'Brake Hoses and Connections', 'GENERAL MAINTENANCE ITEMS'),
                                                      (10, 'Drivetrain (electric motor and gearbox)', 'GENERAL MAINTENANCE ITEMS'),
                                                      (11, 'Suspension System', 'GENERAL MAINTENANCE ITEMS'),
                                                      (12, 'Driveshaft', 'GENERAL MAINTENANCE ITEMS'),
                                                      (13, 'Ball Joints', 'GENERAL MAINTENANCE ITEMS'),
                                                      (14, 'Steering Rack and Tie Rod Ends', 'GENERAL MAINTENANCE ITEMS'),
                                                      (15, 'Cooling Hoses', 'GENERAL MAINTENANCE ITEMS'),
                                                      (16, 'Battery', 'GENERAL MAINTENANCE ITEMS'),
                                                      (17, 'High-Voltage System Cables', 'GENERAL MAINTENANCE ITEMS'),
                                                      (18, 'Charging Port', 'GENERAL MAINTENANCE ITEMS'),
                                                      (19, '12V Battery', 'GENERAL MAINTENANCE ITEMS'),
                                                      (20, 'Wiper Blades / Windshield Washer Fluid', 'GENERAL MAINTENANCE ITEMS');

-- update sequence
SELECT setval('car_models_id_seq', (SELECT MAX(id) FROM car_models));
SELECT setval('maintenance_item_id_seq', (SELECT MAX(id) FROM maintenance_item));


-- Insert data for maintenance_schedule table (generated from logic)
DO $$
    DECLARE
        km_marks INT[] := ARRAY[12000, 24000, 36000, 48000, 60000, 72000, 84000, 96000, 108000, 120000, 132000, 144000, 156000, 168000, 180000, 192000, 204000];
        km INT;
        model RECORD;
        price_multiplier NUMERIC;

    BEGIN
        -- loop through each car model
        FOR model IN SELECT * FROM car_models
            LOOP
                -- set price multiplier based on segment
                price_multiplier := CASE model.segment
                                        WHEN 'Mini car' THEN 0.8
                                        WHEN 'A-SUV' THEN 0.9
                                        WHEN 'B-SUV' THEN 0.95
                                        WHEN 'C-SUV' THEN 1.0 -- Take VF 7 as standard
                                        WHEN 'D-SUV' THEN 1.1
                                        WHEN 'E-SUV' THEN 1.2
                                        ELSE 1.0
                    END;

                -- loop through each kilometer mark
                FOREACH km IN ARRAY km_marks
                    LOOP
                    -- === HANDLING OF SPECIAL RULES ===
                    -- REPLACE
                        INSERT INTO maintenance_schedule (car_model_id, item_id, kilometer_mark, action, price) VALUES (model.id, 1, km, 'REPLACE', 280000 * price_multiplier);
                        -- Brake fluid
                        IF km % 24000 = 0 THEN INSERT INTO maintenance_schedule (car_model_id, item_id, kilometer_mark, action, price) VALUES (model.id, 2, km, 'REPLACE', 250000 * price_multiplier);
                        ELSIF km % 12000 = 0 THEN INSERT INTO maintenance_schedule (car_model_id, item_id, kilometer_mark, action, price) VALUES (model.id, 2, km, 'INSPECT', 50000 * price_multiplier); END IF;

                        -- Air conditioning maintenance
                        IF km % 60000 = 0 THEN INSERT INTO maintenance_schedule (car_model_id, item_id, kilometer_mark, action, price) VALUES (model.id, 3, km, 'REPLACE', 450000 * price_multiplier); END IF;
                        -- Key battery
                        IF km % 24000 = 0 THEN INSERT INTO maintenance_schedule (car_model_id, item_id, kilometer_mark, action, price) VALUES (model.id, 4, km, 'REPLACE', 100000 * price_multiplier); END IF;
                        -- Pin T-Box
                        IF km % 72000 = 0 THEN INSERT INTO maintenance_schedule (car_model_id, item_id, kilometer_mark, action, price) VALUES (model.id, 5, km, 'REPLACE', 150000 * price_multiplier); END IF;
                        -- Coolant
                        IF km = 120000 THEN INSERT INTO maintenance_schedule (car_model_id, item_id, kilometer_mark, action, price) VALUES (model.id, 6, km, 'REPLACE', 350000 * price_multiplier);
                        ELSE INSERT INTO maintenance_schedule (car_model_id, item_id, kilometer_mark, action, price) VALUES (model.id, 6, km, 'INSPECT', 50000 * price_multiplier); END IF;

                        -- === PROCESSING GENERAL ITEMS (ALWAYS INSPECT) ===
                        INSERT INTO maintenance_schedule (car_model_id, item_id, kilometer_mark, action, price) VALUES (model.id, 7, km, 'INSPECT', 150000 * price_multiplier);
                        INSERT INTO maintenance_schedule (car_model_id, item_id, kilometer_mark, action, price) VALUES (model.id, 8, km, 'INSPECT', 80000 * price_multiplier);
                        INSERT INTO maintenance_schedule (car_model_id, item_id, kilometer_mark, action, price) VALUES (model.id, 9, km, 'INSPECT', 50000 * price_multiplier);
                        INSERT INTO maintenance_schedule (car_model_id, item_id, kilometer_mark, action, price) VALUES (model.id, 10, km, 'INSPECT', 100000 * price_multiplier);
                        INSERT INTO maintenance_schedule (car_model_id, item_id, kilometer_mark, action, price) VALUES (model.id, 11, km, 'INSPECT', 100000 * price_multiplier);
                        INSERT INTO maintenance_schedule (car_model_id, item_id, kilometer_mark, action, price) VALUES (model.id, 12, km, 'INSPECT', 80000 * price_multiplier);
                        INSERT INTO maintenance_schedule (car_model_id, item_id, kilometer_mark, action, price) VALUES (model.id, 13, km, 'INSPECT', 80000 * price_multiplier);
                        INSERT INTO maintenance_schedule (car_model_id, item_id, kilometer_mark, action, price) VALUES (model.id, 14, km, 'INSPECT', 120000 * price_multiplier);
                        INSERT INTO maintenance_schedule (car_model_id, item_id, kilometer_mark, action, price) VALUES (model.id, 15, km, 'INSPECT', 50000 * price_multiplier);
                        INSERT INTO maintenance_schedule (car_model_id, item_id, kilometer_mark, action, price) VALUES (model.id, 16, km, 'INSPECT', 200000 * price_multiplier);
                        INSERT INTO maintenance_schedule (car_model_id, item_id, kilometer_mark, action, price) VALUES (model.id, 17, km, 'INSPECT', 100000 * price_multiplier);
                        INSERT INTO maintenance_schedule (car_model_id, item_id, kilometer_mark, action, price) VALUES (model.id, 18, km, 'INSPECT', 50000 * price_multiplier);
                        INSERT INTO maintenance_schedule (car_model_id, item_id, kilometer_mark, action, price) VALUES (model.id, 19, km, 'INSPECT', 80000 * price_multiplier);
                        INSERT INTO maintenance_schedule (car_model_id, item_id, kilometer_mark, action, price) VALUES (model.id, 20, km, 'INSPECT', 50000 * price_multiplier);
                    END LOOP;
            END LOOP;
    END $$;

SELECT 'Database script executed successfully.';