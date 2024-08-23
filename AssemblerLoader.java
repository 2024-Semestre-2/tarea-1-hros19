import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class AssemblerLoader {
    private static final Pattern INSTRUCTION_PATTERN = Pattern.compile(
        "^(MOV|LOAD|STORE|ADD|SUB)\\s+(AX|BX|CX|DX)(?:,\\s*(-?\\d+|AX|BX|CX|DX))?$",
        Pattern.CASE_INSENSITIVE
    );
    
    public List<String> loadAssemblyFile(File asmFile) throws IOException {
        if (!asmFile.exists()) {
            throw new IOException("El archivo no existe: " + asmFile.getPath());
        }
        
        if (!asmFile.isFile()) {
            throw new IOException("La ruta no es un archivo: " + asmFile.getPath());
        }
        
        List<String> lines = Files.readAllLines(asmFile.toPath());
        List<String> validatedLines = validateInstructions(lines);
        
        System.out.println("Archivo de ensamblador cargado: " + asmFile.getName());
        return validatedLines;
    }
    
    private List<String> validateInstructions(List<String> lines) throws IOException {
        List<String> validatedLines = lines.stream()
            .map(String::trim)
            .filter(line -> !line.isEmpty())
            .collect(Collectors.toList());
        
        for (int i = 0; i < validatedLines.size(); i++) {
            String line = validatedLines.get(i);
            if (!INSTRUCTION_PATTERN.matcher(line).matches()) {
                throw new IOException("Error de sintaxis en la línea " + (i + 1) + ": " + line);
            }
            
            if (line.startsWith("MOV")) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    String value = parts[1].trim();
                    if (value.matches("-?\\d+")) {
                        int intValue = Integer.parseInt(value);
                        if (intValue < -128 || intValue > 127) {
                            throw new IOException("Valor fuera del rango de 8 bits en la línea " + (i + 1) + ": " + line);
                        }
                    }
                }
            }
        }
        
        return validatedLines;
    }
}