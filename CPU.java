
import java.util.HashMap;
import java.util.Map;

public class CPU {
    private byte ac;
    private int ir;
    private int pc;
    private byte ax, bx, cx, dx;
    private Memory memory;

    public CPU(Memory memory) {
        this.ac = 0;
        this.ir = 0;
        this.pc = memory.getKernelSize() + memory.getOsSize();
        this.ax = 0;
        this.bx = 0;
        this.cx = 0;
        this.dx = 0;
        this.memory = memory;
    }

    public void executeInstruction(String instruction) {
        String[] parts = instruction.split(" ");
        String opcode = parts[0].toUpperCase();

        switch (opcode) {
            case "MOV":
                if (parts.length != 3) {
                    throw new IllegalArgumentException("MOV requiere 2 argumentos: registro y valor");
                }
                String register = parts[1].toUpperCase().replace(",", "");
                byte value = parseByte(parts[2]);
                movRegister(register, value);
                break;

            case "LOAD":
            case "ADD":
            case "SUB":
            case "STORE":
                if (parts.length != 2) {
                    throw new IllegalArgumentException(opcode + " requiere 1 argumento: registro");
                }
                register = parts[1].toUpperCase();
                switch (opcode) {
                    case "LOAD":
                        loadRegister(register);
                        break;
                    case "ADD":
                        addRegister(register);
                        break;
                    case "SUB":
                        subRegister(register);
                        break;
                    case "STORE":
                        storeRegister(register);
                        break;
                }
                break;

            default:
                throw new IllegalArgumentException("Instrucción desconocida: " + opcode);
        }

        pc++;
    }

    private byte parseByte(String valueStr) {
        int value = Integer.parseInt(valueStr);
        if (value < -128 || value > 127) {
            throw new IllegalArgumentException("Valor fuera de rango: " + valueStr + " (debe estar entre -128 y 127)");
        }
        return (byte) value;
    }

    public void movRegister(String register, byte value) {
        switch (register) {
            case "AX":
                ax = value;
                break;
            case "BX":
                bx = value;
                break;
            case "CX":
                cx = value;
                break;
            case "DX":
                dx = value;
                break;
            default:
                throw new IllegalArgumentException("Registro desconocido: " + register);
        }
    }

    public void loadRegister(String register) {
        switch (register) {
            case "AX":
                ac = memory.getMemoryAt(ax);
                break;
            case "BX":
                ac = memory.getMemoryAt(bx);
                break;
            case "CX":
                ac = memory.getMemoryAt(cx);
                break;
            case "DX":
                ac = memory.getMemoryAt(dx);
                break;
            default:
                throw new IllegalArgumentException("Registro desconocido: " + register);
        }
    }

    public void addRegister(String register) {
        switch (register) {
            case "AX":
                ac = checkByteRange(ac + ax);
                break;
            case "BX":
                ac = checkByteRange(ac + bx);
                break;
            case "CX":
                ac = checkByteRange(ac + cx);
                break;
            case "DX":
                ac = checkByteRange(ac + dx);
                break;
            default:
                throw new IllegalArgumentException("Registro desconocido: " + register);
        }
    }

    public void subRegister(String register) {
        switch (register) {
            case "AX":
                ac = checkByteRange(ac - ax);
                break;
            case "BX":
                ac = checkByteRange(ac - bx);
                break;
            case "CX":
                ac = checkByteRange(ac - cx);
                break;
            case "DX":
                ac = checkByteRange(ac - dx);
                break;
            default:
                throw new IllegalArgumentException("Registro desconocido: " + register);
        }
    }

    public void storeRegister(String register) {
        switch (register) {
            case "AX":
                memory.setMemoryAt(ax, ac);
                break;
            case "BX":
                memory.setMemoryAt(bx, ac);
                break;
            case "CX":
                memory.setMemoryAt(cx, ac);
                break;
            case "DX":
                memory.setMemoryAt(dx, ac);
                break;
            default:
                throw new IllegalArgumentException("Registro desconocido: " + register);
        }
    }

    private byte checkByteRange(int value) {
        if (value < -128 || value > 127) {
            throw new IllegalArgumentException("Resultado fuera de rango: " + value + " (debe estar entre -128 y 127)");
        }
        return (byte) value;
    }

    public byte getAc() {
        return ac;
    }

    public int getIr() {
        return ir;
    }

    public int getPc() {
        return pc;
    }

    public void setPc(int pc) {
        this.pc = pc;
    }

    public byte getAx() {
        return ax;
    }

    public void setAx(byte ax) {
        this.ax = ax;
    }

    public byte getBx() {
        return bx;
    }

    public void setBx(byte bx) {
        this.bx = bx;
    }

    public byte getCx() {
        return cx;
    }

    public void setCx(byte cx) {
        this.cx = cx;
    }

    public byte getDx() {
        return dx;
    }

    public void setDx(byte dx) {
        this.dx = dx;
    }

    public Memory getMemory() {
        return memory;
    }

    public void setMemory(Memory memory) {
        this.memory = memory;
    }

    public void displayRegisters() {
        System.out.println("Estado actual de los registros:");
        System.out.println("AC: " + ac);
        System.out.println("IR: " + ir);
        System.out.println("PC: " + pc);
        System.out.println("AX: " + ax);
        System.out.println("BX: " + bx);
        System.out.println("CX: " + cx);
        System.out.println("DX: " + dx);
    }

    public Map<String, Integer> getRegisterState() {
        Map<String, Integer> registers = new HashMap<>();
        registers.put("AC", (int) ac);
        registers.put("IR", ir);
        registers.put("PC", pc);
        registers.put("AX", (int) ax);
        registers.put("BX", (int) bx);
        registers.put("CX", (int) cx);
        registers.put("DX", (int) dx);
        return registers;
    }
}
