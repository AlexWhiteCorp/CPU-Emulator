package com.alexcorp.study;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Emulator {

    private final int registerSize = 20;

    private Map<String, String> registers;

    private String[] Ins;
    private int PC;
    private int TC;
    private int PS;

    private String[] commands;

    public Emulator() {
        try {
            commands = loadCode("src/main/resources/code.ce").split("\n");
            loadRegisters(new String[]{"R1", "R2", "R3", "R4"});

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            while(true){
                if(reader.readLine() != null){
                    if(TC % 2 == 0)loadCommandToRegister(commands[PC].split(" "));
                    else if(!doCommandFromRegister(Ins)){
                        printError("wrong command!");
                        break;
                    }
                    printState();
                    if(PC == commands.length && TC == 2) break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String loadCode(String path) throws IOException {
        File codeFile = new File(path);
        String code = "";
        if(!codeFile.exists()){
            System.out.println("File not found!");
        }
        BufferedReader reader = new BufferedReader(new FileReader(codeFile));
        while(reader.ready()){
            code += reader.readLine() + "\n";
        }
        System.out.println("Code loaded success. Press \"Enter\" to do comand.");
        return code;
    }

    private void loadCommandToRegister(String[] command){
        System.out.print(" Команда: ");
        printArray(command);
        Ins = command;
        try {
            Ins[2] = intToBinaryString(Integer.parseInt(command[2]), registerSize);
        }
        catch(Exception e){}
        TC = 1;
        PC++;
    }

    private boolean doCommandFromRegister(String[] command){
        if(command.length > 3) return false;
        TC++;
        String value = getValue(command[2]);
        if(command[0].equals("mov")){
           return mov(command[1], value);
        }
        else if(command[0].equals("sal")){
            return sal(command[1], value);
        }
        else if(command[0].equals("sar")){
            return sar(command[1], value);
        }
        return false;
    }

    private void loadRegisters(String[] registersName){
        registers = new HashMap<>();
        for (String s : registersName) {
            registers.put(s, new String("00000000000000000000"));
        }
    }

    private String intToBinaryString(int number, int size){
        boolean minus = number < 0;
        if(minus){
            return Integer.toBinaryString(number).substring(32 - size, 32);
        }
        number = Math.abs(number);
        String res = "";
        for(int i = 0; i < size; i++){
            res = (number % 2) + res;
            number /= 2;
        }
        return res;
    }

    private String getValue(String v){
        try{
            Integer.parseInt(v, 2);
            return v;
        }
        catch(Exception e){
            return registers.get(v);
        }
    }

    private int signum(int n){
        return n >= 0 ? 0 : 1;
    }

    //commands realization
    private boolean mov(String register, String value){
        if(value == null || registers.get(register) == null) return false;

        registers.put(register, value);
        PS = signum(Integer.parseInt(value, 2));
        return true;
    }

    private boolean sal(String register, String value){
        if(value == null || registers.get(register) == null) return false;

        registers.put(register, intToBinaryString((int)(Integer.parseInt(registers.get(register), 2) * Math.pow(2, Integer.parseInt(value, 2))), registerSize));
        PS = signum(Integer.parseInt(value, 2));
        return true;
    }

    private boolean sar(String register, String value){
        if(value == null || registers.get(register) == null) return false;

        registers.put(register, intToBinaryString((int)(Integer.parseInt(registers.get(register), 2) / Math.pow(2, Integer.parseInt(value, 2))), registerSize));
        PS = signum(Integer.parseInt(value, 2));
        return true;
    }

    //out functions
    private void printState(){
        printRegisterInfo("R1");
        System.out.print("      Ins = ");
        printArray(Ins);

        printRegisterInfo("R2");
        System.out.print("       PC = " + PC + "\n");

        printRegisterInfo("R3");
        System.out.print("       TC = " + TC + "\n");

        printRegisterInfo("R4");
        System.out.print("       PS = " + PS + "\n");
    }

    private void printRegisterInfo(String register){
        System.out.print(" " + register + " = ");
        System.out.print(registers.get(register).replaceAll("(.{4})", "$1 "));
    }

    private void printArray(String[] array){
        System.out.print(array[0]);
        for(int i = 1; i < array.length; i++){
            System.out.print(" | " + array[i].replaceAll("(.{4})", "$1 "));
        }
        System.out.print("\n");
    }

    private void printError(String error){
        System.out.println("Run Time Error: " + error);
    }
}