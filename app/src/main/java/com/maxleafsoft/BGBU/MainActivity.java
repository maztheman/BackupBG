package com.maxleafsoft.BGBU;

import android.app.*;
import android.os.Process;
import android.os.*;
import android.view.*;
import android.widget.*;
import java.io.*;
import android.util.Log;
import java.sql.*;
import java.lang.Object;
import android.view.inputmethod.InputMethodManager;
import android.content.*;
import android.text.TextWatcher;
import android.text.Editable;
import java.util.*;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import androidx.fragment.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.core.app.*;
import android.content.pm.PackageManager;
import android.Manifest;
import androidx.core.content.ContextCompat;
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission;
import androidx.activity.ComponentActivity;

public class MainActivity extends ComponentActivity implements OnItemSelectedListener
{

	// Register the permissions callback, which handles the user's response to the
	// system permissions dialog. Save the return value, an instance of
	// ActivityResultLauncher, as an instance variable.
	private ActivityResultLauncher<String> requestPermissionLauncher =
		registerForActivityResult(new RequestPermission(), isGranted -> {
			if (isGranted) {
				// Permission is granted. Continue the action or workflow in your
				// app.
				createGameDropList(new File(GetSdCard() + "Android/data/com.beamdog.baldursgateenhancededition/files/"));
			} else {
				// Explain to the user that the feature is unavailable because the
				// feature requires a permission that the user has denied. At the
				// same time, respect the user's decision. Don't link to system
				// settings in an effort to convince the user to change their
				// decision.
			}
		});

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		
		EditText destPath = (EditText)findViewById(R.id.txtDestPath);
		destPath.setText(getExternalDrive());
		// Attach TextWatcher to EditText
		destPath.addTextChangedListener(mTextEditorWatcher);
		
		Spinner spinner = (Spinner)findViewById(R.id.gameDropList);
		spinner.setOnItemSelectedListener(this);
		
		if (ContextCompat.checkSelfPermission(
			this, Manifest.permission.MANAGE_EXTERNAL_STORAGE) ==
			PackageManager.PERMISSION_GRANTED) {
			// You can use the API that requires the permission.
			createGameDropList(new File(GetSdCard() + "Android/data/com.beamdog.baldursgateenhancededition/files/"));
		}/*/ else if (shouldShowRequestPermissionRationale(...)) {
			// In an educational UI, explain to the user why your app requires this
			// permission for a specific feature to behave as expected, and what
			// features are disabled if it's declined. In this UI, include a
			// "cancel" or "no thanks" button that lets the user continue
			// using your app without granting the permission.
			showInContextUI(...);
		} */else {
			// You can directly ask for the permission.
			// The registered ActivityResultCallback gets the result of this request.
			requestPermissionLauncher.launch(
					Manifest.permission.MANAGE_EXTERNAL_STORAGE);
		}

		setHelpText();
	}

	// EditTextWacther  Implementation
	private final TextWatcher mTextEditorWatcher = new TextWatcher() 
	{
		public void beforeTextChanged(CharSequence s, int start, int count, int after)
		{
			 
		}

		public void onTextChanged(CharSequence s, int start, int before, int count)
		{

		}
		public void afterTextChanged(Editable s)
		{
			setHelpText();
		}
	};
	
	public void resetDestination(View view)
	{
		EditText destPath = (EditText)findViewById(R.id.txtDestPath);
		destPath.setText(getExternalDrive());
	}
	
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, 
		int pos, long id)
	{
		setHelpText(getSourcePath());
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent)
	{
		// TODO: Implement this method
	}
	
	public void createGameDropList(File sourceLocation)
	{
		List<String> gameList = new ArrayList<String>();
		if (sourceLocation.isDirectory())
		{
			//Log.d(sourceLocation.toString(),"src loc is dir");
			String[] children = sourceLocation.list();
			String gameString = "All";
			gameList.add(new String(gameString));
			for(int i = 0; i < sourceLocation.listFiles().length; i++)
			{
				//Log.d(Integer.toString(i),"in for");
				if (new File(sourceLocation, children[i]).isDirectory())
				{
					//Log.d(children[i].toString(),"src loc is dir");
					/*From Dee - Here's the full list:
					save - Baldur's Gate single player
					mpsave - Baldur's Gate multiplayer
					bpsave - Black Pits single player
					mpbpsave - Black Pits multiplayer

					And then here are the other folders that might be in there and worth backing up:
					characters - exported characters
					portraits - custom portraits added by user
					sounds - custom sound sets added by user
					override - small mod files added by user
					*/
					switch(children[i].toString().toLowerCase())
					{
						case "save":
							gameString = "Baldur's Gate (single player)";
							break;
						case "bpsave":
							gameString = "Black Pits (single player)";
							break;
						case "mpsave":
							gameString = "Baldur's Gate (multiplayer)";
							break;
						case "mpbpsave":
							gameString = "Black Pits (multiplayer)";
							break;
						case "characters":
							gameString = "Custom Characters";
							break;
						case "portraits":
							gameString = "Custom Potraits";
							break;
						case "sounds":
							gameString = "Custom Sounds";
							break;
						case "override":
							gameString = "Mod Overrides";
							break;
		
					}
					gameList.add(new String(gameString));
				}
			}
		}
	
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
			android.R.layout.simple_spinner_dropdown_item, gameList);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		// Apply the adapter to the spinner
		Spinner spinner = (Spinner) findViewById(R.id.gameDropList);
		spinner.setAdapter(adapter);
	}
	
	public void setHelpText()
	{
		String helpText = "Source Folder: " + GetSdCard() 
			+ "Android/data/com.beamdog.baldursgateenhancededition/files/" + 
			"\r\n\r\nDestination Folder: " + getDestPath();
			
		helpText += getCurrentTimeStamp();

		TextView editText = (TextView)findViewById(R.id.txtHelp);
		editText.setText(helpText);
	}
	
	public void setHelpText(String sourceFolder)
	{
		String helpText = "Source Folder: " + sourceFolder + 
			"\r\n\r\nDestination Folder: " + getDestPath();
			
		helpText += getCurrentTimeStamp();

		TextView editText = (TextView)findViewById(R.id.txtHelp);
		editText.setText(helpText);
	}
	
	public void toggleTimeStamp(View view)
	{
		setHelpText(getSourcePath());
	}
	
	// gets drive where BG saves are kept
	public String GetSdCard()
	{
		String srcPath = addTrailingSlash(Environment.getExternalStorageDirectory().toString());
			
		return srcPath;
	}
	
	public String addTrailingSlash(String path)
	{
		if (!path.endsWith("/"))
		{
			path += "/";
		}

		return path;
	}
	
	// tries to get external storage (physicsl SD card)
	public String getExternalDrive()
	{
		String extDrive = Environment.getExternalStorageDirectory().toString();
		File file = new File("/storage/extSdCard/");
		
		if (file.isDirectory())
		{
			extDrive = file.getPath().toString();
		}

		return addTrailingSlash(extDrive);
	}
	
	public String getSaveFolder()
	{	
		Spinner spinner = (Spinner)findViewById(R.id.gameDropList);
		String saveFolder = "/"; // Default for All selection
		
		Object selected = spinner.getSelectedItem();

		if (selected != null)
		{
			switch(selected.toString())
			{
				case "Baldur's Gate (single player)":
					saveFolder = "save";
					break;
				case "Black Pits (single player)":
					saveFolder = "bpsave";
					break;
				case "Baldur's Gate (multiplayer)":
					saveFolder = "mpsave";
					break;
				case "Black Pits (multiplayer)":
					saveFolder = "mpbpsave";
					break;
				case "Custom Characters":
					saveFolder = "characters";
					break;
				case "Custom Portraits":
					saveFolder = "portraits";
					break;
				case "Custom Sounds":
					saveFolder = "sounds";
					break;
				case "Mod Overrides":
					saveFolder = "override";
					break;
				default:
					// All
					break;
			}
		}
		
		return addTrailingSlash(saveFolder);
	}
	
	public String getDestPath()
	{
		EditText editText = (EditText)findViewById(R.id.txtDestPath);
		String baseDestPath = editText.getText().toString();
		String destPath = addTrailingSlash(baseDestPath) + "backups/baldur's gate/";
		
		// Check for single slash which means
		// selection was All
		if (getSaveFolder().equals("/"))
		{
			destPath += "All/";
		}
		else
		{
			destPath += getSaveFolder();
		}
		
		return destPath;
	}
	
	public String getSourcePath()
	{
		String sourcePath = GetSdCard() + "Android/data/com.beamdog.baldursgateenhancededition/files/";
		
		// Check for single slash which means
		// selection was All
		if (!getSaveFolder().equals("/"))
		{
			sourcePath += getSaveFolder();
		}

		return sourcePath;
	}

	public void backupFolder(View view)
	{
		TextView editText = (TextView)findViewById(R.id.txtStatus);
		editText.setText("");
		editText.setEnabled(false);
		
		try
		{
			String ts = getCurrentTimeStamp();

			//Log.d(getSourcePath(),"src");
			utilityFunctions.copyFolders(new File(getSourcePath()),
						new File(getDestPath() + ts));
			
			editText.setText("Backup Successful");
		}
		catch (Exception ex)
		{
			editText.setText("Error: " + ex.toString());
		}
	}
	
	public String getCurrentTimeStamp()
	{
		String ts = "Undated/"; // Default if toggle is Off
		Switch toggle = (Switch) findViewById(R.id.swtchTimeStamp);

		if (toggle.isChecked())
		{
			Long time = System.currentTimeMillis();
			Timestamp tsTemp = new Timestamp(time);
			ts = "Dated/" + tsTemp.toString().replace(':',' ').replace('.',' ');
		}
		
		return ts;
	}
	
	public void restoreBackup(View view)
	{
		Intent intent = new Intent(this, restoreFromBackup.class);
		Bundle bundle = new Bundle();
		bundle.putString("srcpath",getSourcePath());
		bundle.putString("dstpath", getDestPath());
		intent.putExtras(bundle);
		startActivity(intent);
	}
}
