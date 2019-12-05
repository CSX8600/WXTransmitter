package com.clussmanproductions.wxradio.gui;

import java.io.IOException;

import com.clussmanproductions.wxradio.tileentity.TransmitterTileEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

public class TransmitterGui extends GuiScreen {
	private TransmitterTileEntity transmitter;
	
	private GuiLabel nameLabel;
	private GuiTextField name;
	private GuiButton close;
	
	public TransmitterGui(TransmitterTileEntity te)
	{
		transmitter = te;
	}
	
	@Override
	public void initGui() {
		int horizontalCenter = width / 2;
		int verticalCenter = height / 2;
		
		int nameFieldSetWidth = 120 + fontRenderer.getStringWidth("Name");
		int nameFieldSetLeft = horizontalCenter - (nameFieldSetWidth / 2);
		int nameFieldSetRight = nameFieldSetLeft + nameFieldSetWidth;
		
		nameLabel = new GuiLabel(fontRenderer, ComponentIDs.NameLabel, nameFieldSetLeft, verticalCenter, fontRenderer.getStringWidth("Name"), 20, 0xFFFFFF);
		name = new GuiTextField(ComponentIDs.Name, fontRenderer, (nameFieldSetRight - 100), verticalCenter, 100, 20);
		name.setText(transmitter.getName());
		
		close = new GuiButton(ComponentIDs.Close, 0, 0, "Close");
		buttonList.add(close);
		
		labelList.add(nameLabel);
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		
		name.drawTextBox();
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		name.mouseClicked(mouseX, mouseY, mouseButton);
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		name.textboxKeyTyped(typedChar, keyCode);
		super.keyTyped(typedChar, keyCode);
	}
	
	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		switch(button.id)
		{
			case ComponentIDs.Close:
				Minecraft.getMinecraft().setIngameFocus();
				break;
		}
	}
	
	@Override
	public void onGuiClosed() {
		
		transmitter.setName(name.getText());
		transmitter.syncClientToServer();
		super.onGuiClosed();
	}
	
	public static class ComponentIDs
	{
		public static final int Name = 0;
		public static final int NameLabel = 1;
		public static final int Close = 2;
	}
}
