
import java.util.ArrayList;
import java.util.List;

public class Memory {
    private byte[] memory;
    private String[] memoryInstructions;
    private int kernelSize;
    private int osSize;
    private int userSize;
    private int userStartIndex;

    public Memory(int totalSize, int kernelSize, int osSize) {
        this.memory = new byte[totalSize];
        this.memoryInstructions = new String[totalSize];
        this.kernelSize = kernelSize;
        this.osSize = osSize;
        this.userSize = totalSize - (kernelSize + osSize);
        this.userStartIndex = kernelSize + osSize;
    }

    public int getKernelSize() {
        return kernelSize;
    }

    public int getOsSize() {
        return osSize;
    }

    public int getUserSize() {
        return userSize;
    }

    public void loadProgram(int[] program, List<String> instructions) {
        if (program.length > userSize) {
            throw new IllegalArgumentException("El programa excede el espacio de memoria asignado para el usuario.");
        }

        for (int i = 0; i < program.length; i++) {
            memory[userStartIndex + i] = (byte) program[i];
            memoryInstructions[userStartIndex + i] = instructions.get(i);
        }
    }

    public byte getMemoryAt(int index) {
        if (index < 0 || index >= memory.length) {
            throw new IndexOutOfBoundsException("Índice fuera del rango de memoria.");
        }
        return memory[index];
    }
    
    public void setMemoryAt(int index, byte value) {
        if (index < 0 || index >= memory.length) {
            throw new IndexOutOfBoundsException("Índice fuera del rango de memoria.");
        }
        memory[index] = value;
    }

    public void displayMemory() {
      System.out.println("Estado actual de la memoria:");
      for (int i = 0; i < memory.length; i++) {
          System.out.printf("Posición %3d: %3d\n", i, memory[i]);
      }
    }

    public List<Memory.MemoryEntry> getMemoryState() {
        List<Memory.MemoryEntry> memoryEntries = new ArrayList<>();
        for (int i = 0; i < memory.length; i++) {
            if (memoryInstructions[i] == null || memoryInstructions
                    [i].equals("0") || memoryInstructions[i].equals("")) {
                memoryEntries.add(new MemoryEntry(i, String.valueOf(memory[i])));
            }
            else {
                memoryEntries.add(new MemoryEntry(i, memoryInstructions[i]));
            }
        }
        return memoryEntries;
    }

    public static class MemoryEntry {
        public int position;
        public String instruction;

        public MemoryEntry(int position, String instruction) {
            this.position = position;
            this.instruction = instruction;
        }
    }

}
