module gep {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    // Spring Boot
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.context;
    requires spring.beans;

    // MongoDB (Spring Data)
    requires spring.data.mongodb;
    requires org.mongodb.driver.core;
    requires org.mongodb.driver.sync.client;
    requires org.mongodb.bson;

    // JWT
    requires jjwt.api;
    requires jjwt.impl;
    requires com.fasterxml.jackson.databind;

    // BCrypt
    requires jbcrypt;
    requires spring.data.commons;

    // Abre pacotes para reflexão
    opens br.edu.ifpr.gep.aplicacao to javafx.fxml, spring.core, com.fasterxml.jackson.databind;
    opens br.edu.ifpr.gep.model to spring.core, com.fasterxml.jackson.databind, javafx.base;

    exports br.edu.ifpr.gep.aplicacao;
    exports br.edu.ifpr.gep.model;
}