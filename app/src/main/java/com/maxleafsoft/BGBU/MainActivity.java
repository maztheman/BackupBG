package com.maxleafsoft.BGBU;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import org.jetbrains.annotations.NotNull;
import android.net.Uri;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION;

public class MainActivity extends AppCompatActivity implements OnItemSelectedListener
{
	private static final int READ_STORAGE_REQUEST_ID = 13893494;
	private final ActivityResultLauncher<String> mRequestPermissionLauncher =
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

	// EditTextWacther  Implementation
	private final TextWatcher mTextEditorWatcher = new TextWatcher() 
	{
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after)
		{
			 
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count)
		{

		}
		@Override
		public void afterTextChanged(Editable s)
		{
			setHelpText();
		}
	};


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		askForPermissions();
		
		EditText destPath = findViewById(R.id.txtDestPath);
		destPath.setText(getExternalDrive());
		// Attach TextWatcher to EditText
		destPath.addTextChangedListener(mTextEditorWatcher);
		
		Spinner spinner = findViewById(R.id.gameDropList);
		spinner.setOnItemSelectedListener(this);

		setHelpText();
	}

	private void askForPermissions()
	{
		if (checkSelfPermission(
				Manifest.permission.READ_EXTERNAL_STORAGE) ==
				PackageManager.PERMISSION_GRANTED) {
			// You can use the API that requires the permission.
			createGameDropList(new File(GetSdCard() + "Android/data/com.beamdog.baldursgateenhancededition/files/"));
		} else if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
			// In an educational UI, explain to the user why your app requires this
			// permission for a specific feature to behave as expected, and what
			// features are disabled if it's declined. In this UI, include a
			// "cancel" or "no thanks" button that lets the user continue
			// using your app without granting the permission.
			//showInContextUI(...);
		} else {
			mRequestPermissionLauncher.launch(
					Manifest.permission.READ_EXTERNAL_STORAGE);
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
			if (Environment.isExternalStorageManager())
			{
				createGameDropList(new File(GetSdCard() + "Android/data/com.beamdog.baldursgateenhancededition/files/"));
			} else {
				Intent intent = new Intent();
				intent.setAction(ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
				Uri uri = Uri.fromParts("package", this.getPackageName(), null);
				intent.setData(uri);
				startActivity(intent);
			}
		} else {
			if (checkSelfPermission(
					Manifest.permission.READ_EXTERNAL_STORAGE) ==
					PackageManager.PERMISSION_GRANTED) {
				// You can use the API that requires the permission.
				createGameDropList(new File(GetSdCard() + "Android/data/com.beamdog.baldursgateenhancededition/files/"));
			} else if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
				// In an educational UI, explain to the user why your app requires this
				// permission for a specific feature to behave as expected, and what
				// features are disabled if it's declined. In this UI, include a
				// "cancel" or "no thanks" button that lets the user continue
				// using your app without granting the permission.
				//showInContextUI(...);
			} else {
				mRequestPermissionLauncher.launch(
							Manifest.permission.READ_EXTERNAL_STORAGE);
			}
		}



	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions,
										   @NotNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == READ_STORAGE_REQUEST_ID) {// If request is cancelled, the result arrays are empty.
			if (grantResults.length > 0 &&
					grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				// Permission is granted. Continue the action or workflow
				// in your app.
				createGameDropList(new File(GetSdCard() + "Android/data/com.beamdog.baldursgateenhancededition/files/"));
			} else {
				// Explain to the user that the feature is unavailable because
				// the feature requires a permission that the user has denied.
				// At the same time, respect the user's decision. Don't link to
				// system settings in an effort to convince the user to change
				// their decision.
			}
			return;
		}
		// Other 'case' lines to check for other
		// permissions this app might request.
	}

	/**
	 * reset destination path
	 * @param view component that stores the display of destination path
	 */
	public void resetDestination(View view)
	{
		EditText destPath = findViewById(R.id.txtDestPath);
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
	
	private void createGameDropList(File sourceLocation)
	{
		List<String> gameList = new ArrayList<String>();
		if (sourceLocation.isDirectory())
		{
			//Log.d(sourceLocation.toString(),"src loc is dir");
			String[] children = sourceLocation.list();
			String gameString = "All";
			gameList.add(gameString);
			for(int i = 0; i < Objects.requireNonNull(sourceLocation.listFiles()).length; i++)
			{
				//Log.d(Integer.toString(i),"in for");
				assert children != null;
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
					switch(children[i].toLowerCase())
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
					gameList.add(gameString);
				}
			}
		}
	
		ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
				android.R.layout.simple_spinner_dropdown_item, gameList);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		// Apply the adapter to the spinner
		Spinner spinner = findViewById(R.id.gameDropList);
		spinner.setAdapter(adapter);
	}
	
	private void setHelpText()
	{
		String helpText = "Source Folder: " + GetSdCard() 
			+ "Android/data/com.beamdog.baldursgateenhancededition/files/" + 
			"\r\n\r\nDestination Folder: " + getDestPath();
			
		helpText += getCurrentTimeStamp();

		TextView editText = findViewById(R.id.txtHelp);
		editText.setText(helpText);
	}
	
	private void setHelpText(String sourceFolder)
	{
		String helpText = "Source Folder: " + sourceFolder + 
			"\r\n\r\nDestination Folder: " + getDestPath();
			
		helpText += getCurrentTimeStamp();

		TextView editText = findViewById(R.id.txtHelp);
		editText.setText(helpText);
	}

	/**
	 * toggle timestamp usage
	 * @param view unused
	 */
	public void toggleTimeStamp(View view)
	{
		setHelpText(getSourcePath());
	}
	
	// gets drive where BG saves are kept
	private static String GetSdCard()
	{
		return addTrailingSlash(Environment.getExternalStorageDirectory().toString());
	}
	
	private static String addTrailingSlash(String path)
	{
		if (!path.endsWith("/"))
		{
			path += "/";
		}

		return path;
	}
	
	// tries to get external storage (physicsl SD card)
	private String getExternalDrive()
	{
		String extDrive = Environment.getExternalStorageDirectory().toString();
		File file = new File("/storage/extSdCard/");
		
		if (file.isDirectory())
		{
			extDrive = file.getPath();
		}

		return addTrailingSlash(extDrive);
	}
	
	private String getSaveFolder()
	{	
		Spinner spinner = findViewById(R.id.gameDropList);
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
		EditText editText = findViewById(R.id.txtDestPath);
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

	@SuppressLint("SetTextI18n")
	public void backupFolder(View view)
	{
		TextView editText = findViewById(R.id.txtStatus);
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
		SwitchCompat toggle = findViewById(R.id.swtchTimeStamp);

		if (toggle.isChecked())
		{
			long time = System.currentTimeMillis();
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
