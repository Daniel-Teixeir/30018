package br.edu.ifpr.gep.model.repository;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import br.edu.ifpr.gep.model.Portaria;
import br.edu.ifpr.gep.model.repository.PortariaPK;
import br.edu.ifpr.gep.model.utils.EmissorTypes;

/**
 * Repositório em memória para gerenciar objetos do tipo Portaria.
 * Permite inserção, busca e listagem com filtros.
 * Carrega e salva dados em JSON.
 */
public class PortariaRepository {

    public static final PortariaRepository INSTANCE = new PortariaRepository();

    private final Map<PortariaPK, Portaria> portarias = new HashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final File dataFile = new File("portarias.json");
    private final PortariaPKKeyDeserializer keyDeserializer = new PortariaPKKeyDeserializer();

    /**
     * Construtor privado para singleton.
     * Configura Jackson e carrega dados do JSON.
     */
    private PortariaRepository() {
        // Registrar módulo para LocalDate
        objectMapper.registerModule(new JavaTimeModule());

        // Configurar para serializar LocalDate corretamente
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Configurar para ignorar erros de serialização de beans vazios
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

        // Registrar serializador/deserializador personalizado para EmissorTypes
        SimpleModule module = new SimpleModule();
        module.addSerializer(EmissorTypes.class, new EmissorTypesSerializer());
        module.addDeserializer(EmissorTypes.class, new EmissorTypesDeserializer());
        objectMapper.registerModule(module);

        System.out.println("Iniciando repositório... Arquivo JSON: " + dataFile.getAbsolutePath());
        loadData();
        System.out.println("Dados carregados: " + portarias.size() + " portarias.");
    }

    /**
     * Carrega dados do arquivo JSON para o mapa em memória.
     */
    private void loadData() {
        if (!dataFile.exists()) {
            System.out.println("Arquivo JSON não encontrado. Iniciando vazio.");
            return;
        }

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> loaded = objectMapper.readValue(dataFile, Map.class);

            for (Map.Entry<String, Object> entry : loaded.entrySet()) {
                String keyStr = entry.getKey();
                Object valueObj = entry.getValue();

                // Desserializar a chave usando o KeyDeserializer
                PortariaPK pk;
                try {
                    pk = (PortariaPK) keyDeserializer.deserializeKey(keyStr, null);
                } catch (Exception e) {
                    System.err.println("Erro ao processar chave '" + keyStr + "': " + e.getMessage());
                    continue;
                }

                // Desserializar o valor como Portaria
                Portaria portaria;
                portaria = objectMapper.convertValue(valueObj, Portaria.class);

                if (portaria.getEmissor() == null) {
                    System.err.println("Emissor nulo para chave '" + keyStr + "'. Pulando.");
                    continue;
                }

                // Verificar se o emissor na PK corresponde ao da Portaria
                if (!pk.getEmissor().equals(portaria.getEmissor().getNome())) {
                    System.err.println("Inconsistência de emissor na chave '" + keyStr + "'. Pulando.");
                    continue;
                }

                portarias.put(pk, portaria);
            }

            System.out.println("Carregadas " + portarias.size() + " portarias válidas de " + loaded.size() + " entradas totais.");
        } catch (IOException e) {
            System.err.println("Erro ao carregar dados do JSON: " + e.getMessage());
            portarias.clear(); // Inicia vazio em caso de erro
        }
    }

    /**
     * Salva dados do mapa para o arquivo JSON.
     * Chamado automaticamente em operações de modificação, se necessário.
     */
    private void saveData() {
        try {
            objectMapper.writeValue(dataFile, portarias);
            System.out.println("Dados salvos no JSON: " + portarias.size() + " portarias.");
        } catch (IOException e) {
            System.err.println("Erro ao salvar dados no JSON: " + e.getMessage());
        }
    }

    /**
     * Insere uma nova portaria no repositório.
     * Evita duplicidade de chave (emissor + número + ano).
     * Salva no JSON após inserção.
     */
    public boolean insert(Portaria portaria) {
        if (portaria.getEmissor() == null) {
            System.err.println("Emissor nulo na inserção. Operação cancelada.");
            return false;
        }

        PortariaPK pk = new PortariaPK(
                portaria.getEmissor().getNome(),
                portaria.getNumero(),
                portaria.getPublicacao().getYear()
        );

        if (portarias.containsKey(pk)) {
            System.err.println("Portaria já existe: " + pk);
            return false; // Já existe
        }

        portarias.put(pk, portaria);
        saveData(); // Salva após inserção
        return true;
    }

    /** Atualiza uma portaria existente */
    public boolean update(Portaria portaria) {
        if (portaria.getEmissor() == null) {
            System.err.println("Emissor nulo na atualização. Operação cancelada.");
            return false;
        }

        PortariaPK pk = new PortariaPK(
                portaria.getEmissor().getNome(),
                portaria.getNumero(),
                portaria.getPublicacao().getYear()
        );
        if (portarias.containsKey(pk)) {
            portarias.put(pk, portaria);
            saveData(); // Salva após atualização
            return true;
        }
        return false;
    }

    /** Deleta uma portaria pela chave */
    public boolean delete(String emissorNome, Integer numero, Integer ano) {
        PortariaPK pk = new PortariaPK(emissorNome, numero, ano);
        boolean removed = portarias.remove(pk) != null;
        if (removed) {
            saveData(); // Salva após deleção
        }
        return removed;
    }

    /** Deleta todas as portarias */
    public int deleteAll() {
        int size = portarias.size();
        portarias.clear();
        saveData(); // Salva após limpar
        return size;
    }

    /** Busca uma portaria pela chave */
    public Optional<Portaria> findPortaria(String emissorNome, Integer numero, Integer ano) {
        PortariaPK pk = new PortariaPK(emissorNome, numero, ano);
        return Optional.ofNullable(portarias.get(pk));
    }

    /** Retorna todas as portarias */
    public List<Portaria> findAll() {
        return new ArrayList<>(portarias.values());
    }

    /** Busca por emissor */
    public List<Portaria> findByEmissor(String emissor, boolean strict) {
        return portarias.values().stream()
                .filter(p -> {
                    if (p.getEmissor() == null) return false;
                    return search(p.getEmissor().getNome(), emissor, strict);
                })
                .collect(Collectors.toList());
    }

    /** Busca por ano */
    public List<Portaria> findByAno(Integer ano) {
        return portarias.values().stream()
                .filter(p -> p.getPublicacao().getYear() == ano)
                .collect(Collectors.toList());
    }

    /** Busca por membro */
    public List<Portaria> findByMembro(String membro, boolean strict) {
        return portarias.values().stream()
                .filter(p -> search(p.getMembro(), membro, strict))
                .collect(Collectors.toList());
    }

    /** Busca por número */
    public List<Portaria> findByNumero(Integer numero) {
        return portarias.values().stream()
                .filter(p -> p.getNumero().equals(numero))
                .collect(Collectors.toList());
    }

    /** Busca por data de publicação exata */
    public List<Portaria> findByPublicacao(LocalDate data) {
        return portarias.values().stream()
                .filter(p -> p.getPublicacao().equals(data))
                .collect(Collectors.toList());
    }

    /** Busca por período (inclusive) */
    public List<Portaria> findByPeriodo(LocalDate start, LocalDate end) {
        return portarias.values().stream()
                .filter(p -> !p.getPublicacao().isBefore(start) && !p.getPublicacao().isAfter(end))
                .collect(Collectors.toList());
    }

    private boolean search(String field, String value, boolean strict) {
        if (field == null || value == null) return false;
        return strict ?
                field.equalsIgnoreCase(value) :
                field.toLowerCase().contains(value.toLowerCase());
    }

    /**
     * Serializador personalizado para EmissorTypes.
     */
    private static class EmissorTypesSerializer extends StdSerializer<EmissorTypes> {
        public EmissorTypesSerializer() {
            this(null);
        }

        public EmissorTypesSerializer(Class<EmissorTypes> t) {
            super(t);
        }

        @Override
        public void serialize(EmissorTypes value,
                              com.fasterxml.jackson.core.JsonGenerator gen,
                              com.fasterxml.jackson.databind.SerializerProvider provider) throws IOException {
            if (value != null) {
                gen.writeString(value.getNome());
            } else {
                gen.writeNull();
            }
        }
    }

    /**
     * Deserializador personalizado para EmissorTypes.
     */
    private static class EmissorTypesDeserializer extends StdDeserializer<EmissorTypes> {
        public EmissorTypesDeserializer() {
            this(null);
        }

        public EmissorTypesDeserializer(Class<?> vc) {
            super(vc);
        }

        @Override
        public EmissorTypes deserialize(com.fasterxml.jackson.core.JsonParser p,
                                        com.fasterxml.jackson.databind.DeserializationContext ctxt) throws IOException {
            String name = p.getValueAsString();
            if (name == null || name.trim().isEmpty()) {
                return null;
            }
            return EmissorTypes.fromName(name);
        }
    }
}