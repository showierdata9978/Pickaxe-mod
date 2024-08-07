package tech.showierdata.pickaxe.ui;

import imgui.ImGui;

import imgui.ImVec2;
import imgui.extension.texteditor.TextEditor;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiWindowFlags;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;
import tech.showierdata.pickaxe.Pickaxe;
import xyz.breadloaf.imguimc.interfaces.Renderable;
import xyz.breadloaf.imguimc.interfaces.Theme;
import xyz.breadloaf.imguimc.theme.ImGuiDarkTheme;
import xyz.breadloaf.imguimc.Imguimc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class NoteEditor implements Renderable {
    private final TextEditor textEditor;
    private final Pickaxe pickaxe;
    public String text;
    public boolean isOpen;
    public boolean isFocused = false;
    private boolean helpScreenOpen = false;

    public static Path SAVE_FILE_PATH = Path.of("./config/pickaxe.notes.txt");

    public NoteEditor(Pickaxe pickaxe) {
        if (Files.exists(SAVE_FILE_PATH)) {
            try {
                this.text = Files.readString(SAVE_FILE_PATH);
            } catch (IOException e) {
                this.text = "Failed to load saved notes!\n\n" + e;
                Pickaxe.LOGGER.error("", e);
            }
        } else {
            this.text = "";
        }
        this.textEditor = new TextEditor();
        this.textEditor.setText(this.text);
        this.pickaxe = pickaxe;
        this.isOpen = false;
    }

    @Override
    public String getName() {
        return "Note Editor - Pickaxe Mod";
    }

    public void flip() {
        if (this.isOpen) {
            Imguimc.pullRenderable(this);
        } else {
            Imguimc.pushRenderable(this);
            MinecraftClient client = MinecraftClient.getInstance();
            ImGui.setNextWindowSize(client.getWindow().getHeight(), client.getWindow().getWidth() / 5f, ImGuiCond.Always);
            ImGui.setNextWindowPos(0, 0, ImGuiCond.Always);
            ImGui.setWindowFocus();
            this.textEditor.setHandleKeyboardInputs(true);

        }
        this.isOpen = !this.isOpen;
    }

    public void save() {
        try {
            if (Files.notExists(SAVE_FILE_PATH)) {
                Files.createFile(SAVE_FILE_PATH);
            }
            Files.writeString(SAVE_FILE_PATH, this.text);
            ImGui.text("Saved!");
        } catch (IOException e) {
            Pickaxe.LOGGER.error("", e);
            ImGui.text("Failed to save :( (check logs for details)");
        }
    }

    public String getId() {
        return "pickmod-notes";
    }

    @Override
    public Theme getTheme() {
        return new ImGuiDarkTheme();
    }

    private void renderHelpPage(int id) {
        ImGui.begin("Help - Pickaxe Mod", ImGuiWindowFlags.AlwaysAutoResize);
        String text = """
                      Hi, Welcome to pickaxe mod's note editor!  Make sure to save your data!
                     
                      Having the editor within the game screen is unsupported.
                      
                      This UI was made with imgui-java (v1.86.12) with imgui-mc (v1.0.7)
                      """;
        ImVec2 textSize = ImGui.calcTextSize(text);
        ImGui.text(text);
        ImGui.end();
    }

    private void renderTabBar() {
        if (ImGui.button("Help")) {
            helpScreenOpen = !helpScreenOpen;
        }

        if (ImGui.beginMenu("Edit")) {
            if (ImGui.menuItem("Undo", "CTRL+Z"))
                textEditor.undo(1);
            if (ImGui.menuItem("Redo", "CTRL+Y"))
                textEditor.redo(1);
            ImGui.separator();
            if (ImGui.menuItem("Cut", "CTRL+X"))
                textEditor.cut();
            if (ImGui.menuItem("Copy", "CTRL+C"))
                textEditor.copy();
            if (ImGui.menuItem("Paste", "CTRL+V"))
                textEditor.paste();
            ImGui.endMenu();
        }
        if (ImGui.button("Save")) {
            ImGui.openPopup("saved");
        }
        if (ImGui.beginPopup("saved")) {
            save();
            ImGui.endPopup();
        }
    }

    @Override
    public void render() {

        if (!this.pickaxe.isInPickaxe()) {
            Imguimc.pullRenderableAfterRender(this);
            this.isOpen = false;
        }

        MinecraftClient client = MinecraftClient.getInstance();


        ImGui.begin(getName(), ImGuiWindowFlags.MenuBar);
        this.isFocused = ImGui.isWindowFocused(ImGui.getID(getName()));

        if (ImGui.beginMenuBar()) {
            renderTabBar();
            ImGui.endMenuBar();
        };
        this.textEditor.render(getName());

        if (ImGui.isKeyPressed(GLFW.GLFW_KEY_BACKSPACE)) {
            this.textEditor.delete();
        }

        if (this.textEditor.isTextChanged()) {
            this.text = this.textEditor.getText();
        }

        int id = ImGui.getWindowDockID();
        ImGui.end();


        if (helpScreenOpen) renderHelpPage(id);


    }

}
