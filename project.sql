.mode column
.headers ON

create table customer (cust_id integer, cust_name varchar(200), address varchar(250), phone bigint, email varchar(50), primary key(cust_id asc));
create table c_order (order_id integer, cust_id int, type varchar(50), amount int, paid int, deliver int, primary key(order_id asc), foreign key(cust_id) references customer(cust_id), foreign key(type) references cookie(type));
create table scout (scout_id integer, scout_name varchar(100), age int, primary key(scout_id asc));
create table cookie (type varchar(200), price float, primary key(type));

insert into customer values (null, "Rudy Duarte", "200 W College St", "7653214321", "rudy@test.com");
insert into customer values (null, "Long Pham", "906 W Wabash Ave", "9783190044", "long@test.com");
insert into customer values (null, "Matt Schramm", "301 W Wabash Ave", "7653191234", "matt@test.com");
insert into customer values (null, "Wade Miller", "114 W College St", "7654014731", "wade@test.com");
insert into customer values (null, "David Chew", "114 W College St", "7653214321", "david@test.com");

insert into c_order values (null, 1, "Trefoils", 2, 1, 1);
insert into c_order values (null, 1, "Savannah Smiles", 5, 0, 1);
insert into c_order values (null, 3, "Thank U Berry Munch", 10, 1, 0);
insert into c_order values (null, 4, "Trefoils", 2, 0, 1);
insert into c_order values (null, 4, "Samoas", 4, 0, 1);
insert into c_order values (null, 5, "Tagalongs", 5, 1, 1);

insert into scout values (null, "Tiffany", 12);
insert into scout values (null, "Anne", 10);
insert into scout values (null, "Abigail", 11);
insert into scout values (null, "Carolyn", 9);
insert into scout values (null, "Elizabeth", 12);
insert into scout values (null, "Mandy", 10);

insert into cookie values ("Savannah Smiles", 4.00);
insert into cookie values ("Trefoils", 4.00);
insert into cookie values ("Do-Si-Dos", 4.00);
insert into cookie values ("Samoas", 4.00);
insert into cookie values ("Dulce De Leche", 4.00);
insert into cookie values ("Thank U Berry Munch", 4.00);
insert into cookie values ("Tagalongs", 4.00);
insert into cookie values ("Thin Mints", 4.00);