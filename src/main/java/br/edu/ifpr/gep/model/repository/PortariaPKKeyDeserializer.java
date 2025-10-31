package br.edu.ifpr.gep.model.repository;

import java.io.IOException;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;

public class PortariaPKKeyDeserializer extends KeyDeserializer {

    @Override
    public Object deserializeKey(String key, DeserializationContext ctxt) throws IOException {
        // Espera formato: PortariaPK [emissor=mpf, numero=3, ano=2005]
        try {
            if (key.startsWith("PortariaPK [") && key.endsWith("]")) {
                String content = key.substring(12, key.length() - 1); // remove "PortariaPK [" e "]"
                String[] parts = content.split(",\\s*");

                String emissor = null;
                Integer numero = null;
                Integer ano = null;

                for (String part : parts) {
                    String[] kv = part.split("=");
                    switch (kv[0].trim()) {
                        case "emissor":
                            emissor = kv[1].trim();
                            break;
                        case "numero":
                            numero = Integer.parseInt(kv[1].trim());
                            break;
                        case "ano":
                            ano = Integer.parseInt(kv[1].trim());
                            break;
                    }
                }

                if (emissor != null && numero != null && ano != null) {
                    return new PortariaPK(emissor, numero, ano);
                }
            }

            throw new IOException("Formato inválido para PortariaPK: " + key);
        } catch (Exception e) {
            throw new IOException("Erro ao desserializar PortariaPK: " + key, e);
        }
    }
}
