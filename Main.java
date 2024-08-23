
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Main {

    private static JTextArea instructionArea;
    private static JTable memoryTable;
    private static JTable cpuTable;
    private static JTable pcbTable;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> createAndShowGUI(args));
    }

    private static void createAndShowGUI(String[] args) {
        JFrame frame = new JFrame("Simulador de CPU");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        JPanel mainPanel = new JPanel(new BorderLayout());

        instructionArea = new JTextArea(5, 50);
        instructionArea.setEditable(false);
        JScrollPane instructionScrollPane = new JScrollPane(instructionArea);
        mainPanel.add(instructionScrollPane, BorderLayout.NORTH);

        JPanel tablesPanel = new JPanel(new GridLayout(1, 3));

        memoryTable = new JTable();
        JScrollPane memoryScrollPane = new JScrollPane(memoryTable);
        tablesPanel.add(memoryScrollPane);

        cpuTable = new JTable();
        JScrollPane cpuScrollPane = new JScrollPane(cpuTable);
        tablesPanel.add(cpuScrollPane);

        pcbTable = new JTable();
        JScrollPane pcbScrollPane = new JScrollPane(pcbTable);
        tablesPanel.add(pcbScrollPane);

        mainPanel.add(tablesPanel, BorderLayout.CENTER);

        JButton loadButton = new JButton("Cargar Archivo");
        JButton nextButton = new JButton("Siguiente Instrucción");
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(loadButton);
        buttonPanel.add(nextButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        frame.add(mainPanel);
        frame.setVisible(true);

        SimulationController controller = new SimulationController(loadButton, nextButton);
        
        loadButton.addActionListener(e -> controller.loadFile());
        nextButton.addActionListener(e -> controller.nextInstruction());
    }

    private static void appendText(String text) {
        instructionArea.append(text + "\n");
        instructionArea.setCaretPosition(instructionArea.getDocument().getLength());
    }

    private static void updateMemoryTable(List<Memory.MemoryEntry> memoryState) {
        DefaultTableModel model = new DefaultTableModel(new Object[]{"Posición", "Instrucción"}, 0);
        for (Memory.MemoryEntry entry : memoryState) {
            model.addRow(new Object[]{entry.position, entry.instruction});
        }
        memoryTable.setModel(model);
    }

    private static void updateCPUTable(Map<String, Integer> registerState) {
        DefaultTableModel model = new DefaultTableModel(new Object[]{"Registro", "Valor"}, 0);
        for (Map.Entry<String, Integer> entry : registerState.entrySet()) {
            model.addRow(new Object[]{entry.getKey(), entry.getValue()});
        }
        cpuTable.setModel(model);
    }

    private static void updatePCBTable(Map<String, Integer> pcbState) {
        DefaultTableModel model = new DefaultTableModel(new Object[]{"Campo", "Valor"}, 0);
        for (Map.Entry<String, Integer> entry : pcbState.entrySet()) {
            model.addRow(new Object[]{entry.getKey(), entry.getValue()});
        }
        pcbTable.setModel(model);
    }

    private static class SimulationController {
        private int currentInstructionIndex = 0;
        private List<String> instructions;
        private Memory memory;
        private CPU cpu;
        private PCB pcb;
        private JButton loadButton;
        private JButton nextButton;

        public SimulationController(JButton loadButton, JButton nextButton) {
            this.loadButton = loadButton;
            this.nextButton = nextButton;
            this.nextButton.setEnabled(false);
        }

        public void loadFile() {
            int totalMemorySize = askForMemorySize("Ingrese el tamaño total de la memoria:", 20);
            int kernelSize = askForMemorySize("Ingrese el tamaño del kernel:", 4);
            int osSize = askForMemorySize("Ingrese el tamaño del sistema operativo:", 4);

            if (totalMemorySize <= 0 || kernelSize <= 0 || osSize <= 0 || kernelSize + osSize >= totalMemorySize) {
                JOptionPane.showMessageDialog(null, "Error: Los tamaños ingresados no son válidos. Intente de nuevo.");
                return;
            }

            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                try {
                    AssemblerLoader assemblerLoader = new AssemblerLoader();
                    instructions = assemblerLoader.loadAssemblyFile(selectedFile);

                    memory = new Memory(totalMemorySize, kernelSize, osSize);

                    int[] program = new int[instructions.size()];
                    for (int i = 0; i < instructions.size(); i++) {
                        program[i] = i + 1;
                    }
                    memory.loadProgram(program, instructions);

                    pcb = new PCB(1, memory.getKernelSize() + memory.getOsSize());
                    cpu = new CPU(memory);

                    updateTables();
                    currentInstructionIndex = 0;
                    nextButton.setEnabled(true);
                    appendText("Archivo cargado: " + selectedFile.getName());
                } catch (IOException e) {
                    appendText("Error al cargar el archivo: " + e.getMessage());
                } catch (Exception e) {
                    appendText("Error de ejecución: " + e.getMessage());
                }
            }
        }

        public void nextInstruction() {
            if (currentInstructionIndex < instructions.size()) {
                String instruction = instructions.get(currentInstructionIndex);
                appendText("Ejecutando instrucción: " + instruction);

                cpu.executeInstruction(instruction);
                pcb.saveState(cpu);

                updateTables();

                currentInstructionIndex++;
            } else {
                appendText("Ejecución del programa completada.");
                nextButton.setEnabled(false);
            }
        }

        private void updateTables() {
            updateMemoryTable(memory.getMemoryState());
            updateCPUTable(cpu.getRegisterState());
            updatePCBTable(pcb.getPCBState());
        }

        private int askForMemorySize(String message, int defaultValue) {
            String input = JOptionPane.showInputDialog(null, message, defaultValue);
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Entrada inválida. Usando valor predeterminado: " + defaultValue);
                return defaultValue;
            }
        }
    }
}
