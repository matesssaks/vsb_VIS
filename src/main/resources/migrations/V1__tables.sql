CREATE TABLE users (
	user_id serial PRIMARY KEY,
	name varchar(50) NOT NULL,
	email varchar(50) UNIQUE NOT NULL,
	password varchar(255) NOT NULL,
	CHECK (email LIKE '%_@_%._%')
);

CREATE TABLE person (
	person_id serial PRIMARY KEY,
	user_id int NOT NULL REFERENCES users (user_id),
	personal_number varchar(20) UNIQUE NOT NULL,
	name varchar(50) NOT NULL,
	town varchar(50) NOT NULL,
	street varchar(50) NOT NULL,
	postal_code varchar(10) NOT NULL,
	born_date timestamptz NOT NULL,
	CHECK (born_date < current_timestamp)
);

CREATE TABLE camp (
	camp_id serial PRIMARY KEY,
	name varchar(50) NOT NULL,
	description varchar(1024) NOT NULL,
	img_url varchar(255) NOT NULL,
	start_date timestamptz NOT NULL,
	end_date timestamptz NOT NULL,
	price decimal NOT NULL,
	capacity int NOT NULL,
	registered int NOT NULL DEFAULT (0)
	CHECK (price >= 0)
	CHECK (capacity >= 0)
	CHECK (registered >= 0)
	CHECK (start_date < end_date)
);

CREATE TABLE role (
	role_id serial PRIMARY KEY,
	name varchar(20) NOT NULL,
	can_edit_camp boolean NOT NULL,
	can_create_task boolean NOT NULL,
	can_edit_task boolean NOT NULL,
	can_view_drugs boolean NOT NULL,
	can_receive_info boolean NOT NULL
);

CREATE TABLE participant (
	participant_id serial PRIMARY KEY,
	camp_id int NOT NULL REFERENCES camp (camp_id),
	person_id int NOT NULL REFERENCES person (person_id),
	role_id int NOT NULL DEFAULT (1) REFERENCES role (role_id),
	UNIQUE ("camp_id", "person_id")
);

/*
CREATE TABLE task (
	task_id serial PRIMARY KEY,
	short_description varchar(20) NOT NULL,
	description varchar(255),
	created_at timestampz NOT NULL DEFAULT (getdate()),
	start_date timestamptz,
	finished_at timestamptz,
	due_date timestampz
);
 */

CREATE TABLE participant_task (
	participant_id int NOT NULL REFERENCES participant (participant_id),
	task_id int NOT NULL,
	PRIMARY KEY (participant_id, task_id)
);

CREATE TABLE drug (
	drug_id serial PRIMARY KEY,
	person_id int NOT NULL REFERENCES person (person_id),
	name varchar(50) NOT NULL,
	dosage int NOT NULL,
	description varchar(255)
);


