package ui;

public interface IWindow {
	
	public void showWindow();
	public void hideWindow();
	void showWindow(IWindow prev);
}
