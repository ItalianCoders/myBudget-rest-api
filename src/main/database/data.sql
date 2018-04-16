INSERT INTO public.users(
	username, password, email, firstname, lastname, alias, gender, socialtype)
	VALUES ('admin', '$2a$04$SCCEMlPMLDQuxwET5TFWOev8298omHKsRvjqA/8aZhfJzo92tKozi','dario.frongillo@gmail.com', 'dario','frongillo', 'frongy', 0, 0);

insert into accounts
(id,name,description,status, DEFAULT_USER)
values('0','Account.default.name','Account.default.description', 0,'admin');

insert into  USER_ACCOUNT values ( 'admin','0');


INSERT INTO public.categories (id, type, user_value, is_editable, account_id, createdat, updatedat, iconid) VALUES ('0', 0, 'tag.incoming.saving', false, NULL, 1522327674, 1523306606, 0);
INSERT INTO public.categories (id, type, user_value, is_editable, account_id, createdat, updatedat, iconid) VALUES ('1', 0, 'tag.incoming.salary', false, NULL, 1522327674, 1523306606, 1);
INSERT INTO public.categories (id, type, user_value, is_editable, account_id, createdat, updatedat, iconid) VALUES ('2', 0, 'tag.incoming.gift', false, NULL, 1522327674, 1523306606, 2);
INSERT INTO public.categories (id, type, user_value, is_editable, account_id, createdat, updatedat, iconid) VALUES ('3', 1, 'tag.expense.clothes', false, NULL, 1522327777, 1523306606, 3);
INSERT INTO public.categories (id, type, user_value, is_editable, account_id, createdat, updatedat, iconid) VALUES ('4', 1, 'tag.expense.animal', false, NULL, 1522327777, 1523306606, 4);
INSERT INTO public.categories (id, type, user_value, is_editable, account_id, createdat, updatedat, iconid) VALUES ('5', 1, 'tag.expense.auto', false, NULL, 1522327777, 1523306606, 5);
INSERT INTO public.categories (id, type, user_value, is_editable, account_id, createdat, updatedat, iconid) VALUES ('6', 1, 'tag.expense.bills', false, NULL, 1522327777, 1523306606, 6);
INSERT INTO public.categories (id, type, user_value, is_editable, account_id, createdat, updatedat, iconid) VALUES ('7', 1, 'tag.expense.house', false, NULL, 1522327777, 1523306606, 7);
INSERT INTO public.categories (id, type, user_value, is_editable, account_id, createdat, updatedat, iconid) VALUES ('8', 1, 'tag.expense.food', false, NULL, 1522327777, 1523306606, 8);
INSERT INTO public.categories (id, type, user_value, is_editable, account_id, createdat, updatedat, iconid) VALUES ('9', 1, 'tag.expense.tel', false, NULL, 1522327857, 1523306606, 9);
INSERT INTO public.categories (id, type, user_value, is_editable, account_id, createdat, updatedat, iconid) VALUES ('10', 1, 'tag.expense.bodyCare', false, NULL, 1522327857, 1523306606, 10);
INSERT INTO public.categories (id, type, user_value, is_editable, account_id, createdat, updatedat, iconid) VALUES ('11', 1, 'tag.expense.dinner', false, NULL, 1522327857, 1523306606, 11);
INSERT INTO public.categories (id, type, user_value, is_editable, account_id, createdat, updatedat, iconid) VALUES ('12', 1, 'tag.expense.gift', false, NULL, 1522327857, 1523306606, 12);
INSERT INTO public.categories (id, type, user_value, is_editable, account_id, createdat, updatedat, iconid) VALUES ('13', 1, 'tag.expense.health', false, NULL, 1522327943, 1523306606, 13);
INSERT INTO public.categories (id, type, user_value, is_editable, account_id, createdat, updatedat, iconid) VALUES ('14', 1, 'tag.expense.sport', false, NULL, 1522327954, 1523306606, 14);
INSERT INTO public.categories (id, type, user_value, is_editable, account_id, createdat, updatedat, iconid) VALUES ('15', 1, 'tag.expense.transportation', false, NULL, 1522327970, 1523306606, 15);
INSERT INTO public.categories (id, type, user_value, is_editable, account_id, createdat, updatedat, iconid) VALUES ('16', 0, 'tag.expense.general', false, NULL, 1522327970, 1523306606, 16);
INSERT INTO public.categories (id, type, user_value, is_editable, account_id, createdat, updatedat, iconid) VALUES ('17', 1, 'tag.incoming.general', false, NULL, 1522327970, 1523306660, 16);
