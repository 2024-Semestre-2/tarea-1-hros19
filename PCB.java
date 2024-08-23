
import java.util.HashMap;
import java.util.Map;

public class PCB {
    private int processId;
    private int pc;
    private byte ax, bx, cx, dx;
    private int memoryStart;

    public PCB(int processId, int memoryStart) {
        this.processId = processId;
        this.pc = 0;
        this.ax = 0;
        this.bx = 0;
        this.cx = 0;
        this.dx = 0;
        this.memoryStart = memoryStart;
    }

    public void saveState(CPU cpu) {
        this.pc = cpu.getPc();
        this.ax = cpu.getAx();
        this.bx = cpu.getBx();
        this.cx = cpu.getCx();
        this.dx = cpu.getDx();
    }

    public void restoreState(CPU cpu) {
        cpu.setPc(this.pc);
        cpu.setAx(this.ax);
        cpu.setBx(this.bx);
        cpu.setCx(this.cx);
        cpu.setDx(this.dx);
    }

    public void displayAttributes() {
        System.out.println("Process ID: " + processId);
        System.out.println("Program Counter: " + pc);
        System.out.println("AX: " + ax);
        System.out.println("BX: " + bx);
        System.out.println("CX: " + cx);
        System.out.println("DX: " + dx);
        System.out.println("Memory Start: " + memoryStart);
    }

    public Map<String, Integer> getPCBState() {
    Map<String, Integer> pcbState = new HashMap<>();
        pcbState.put("Process ID", processId);
        pcbState.put("Program Counter", pc);
        pcbState.put("Memory Start", memoryStart);
        return pcbState;
    }

    public int getProcessId() {
        return processId;
    }

    public int getPc() {
        return pc;
    }

    public byte getAx() {
        return ax;
    }

    public byte getBx() {
        return bx;
    }

    public byte getCx() {
        return cx;
    }

    public byte getDx() {
        return dx;
    }

    public int getMemoryStart() {
        return memoryStart;
    }
}
