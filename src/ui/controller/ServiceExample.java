package ui.controller;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class ServiceExample extends Service<String> {
	@Override
	protected Task<String> createTask() {
		return new Task<String>() {
			@Override
			protected String call() throws Exception {
				Thread.sleep(1000);
				return null;
			}
		};
	}
}