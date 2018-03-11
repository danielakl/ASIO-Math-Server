package server.handler;


import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

interface Handler extends Runnable {
    ScriptEngine scriptEngine = (new ScriptEngineManager()).getEngineByName("JavaScript");
}
