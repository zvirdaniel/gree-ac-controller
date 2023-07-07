const apiUrl = getApiUrl();

const LAST_DEVICE_COOKIE_NAME = 'last-gree-device-mac';

// Get DOM elements
const deviceSelect = document.getElementById('deviceSelect');
const powerButton = document.getElementById('powerButton');
const increaseTempButton = document.getElementById('increaseTemp');
const decreaseTempButton = document.getElementById('decreaseTemp');
const temperatureDisplay = document.getElementById('temperature');
const windSpeedSelect = document.getElementById('windSpeed');
const horizontalSwingSelect = document.getElementById('horizontalSwing');
const verticalSwingSelect = document.getElementById('verticalSwing');

// Initialize state
let deviceMac;
let isPowerOn;
let temperature;
let temperatureSensor;
let windSpeed;
let horizontalSwing;
let verticalSwing;
let quietMode;
let turboMode;
let sleepMode;

// Update UI from state
function updateUI() {
    powerButton.textContent = isPowerOn ? 'On' : 'Off';
    temperatureDisplay.textContent = temperature + 'C';
    windSpeedSelect.value = windSpeed;
    horizontalSwingSelect.value = horizontalSwing;
    verticalSwingSelect.value = verticalSwing;
}

// Update state from response
function updateState(data) {
    if (data != null) {
        // Update the control values based on the received data
        isPowerOn = data.power;
        temperature = data.temperature;
        temperatureSensor = data.temperatureSensor;
        windSpeed = data.fanSpeed;
        verticalSwing = data.verticalSwingDirection;
        horizontalSwing = data.horizontalSwingDirection;
        quietMode = data.quiet;
        turboMode = data.turbo;
        sleepMode = data.sleepMode;

        // Update the UI
        updateUI();
    }
}

// Fetch data from API and update controls
function fetchGreeDeviceData() {
    // Fetch data from the API endpoint
    fetch(`${apiUrl}/climate/gree/connected-devices/device-status/${(deviceSelect.value)}`)
        .then(response => response.json())
        .then(json => updateState(json))
        .catch(error => console.log('Error:', error));
}

function updateGreeDeviceData(option, params) {
    fetch(`${apiUrl}/climate/gree/connected-devices/${option}?` + params, {method: 'PATCH'})
        .then(response => response.json())
        .then(json => updateState(json[0]))
        .catch(error => console.log('Error:', error));
}

// Event listeners
deviceSelect.addEventListener('change', function () {
    deviceMac = deviceSelect.value;
    Cookies.set(LAST_DEVICE_COOKIE_NAME, deviceMac);
    fetchGreeDeviceData();
});

powerButton.addEventListener('click', function () {
    // Update the power state on the server
    let params = new URLSearchParams({
        mac: deviceMac,
        online: !isPowerOn,
    });
    updateGreeDeviceData('power', params);
});

increaseTempButton.addEventListener('click', function () {
    // Update the temperature on the server
    let params = new URLSearchParams({
        mac: deviceMac,
        temperature: ++temperature,
    });
    updateGreeDeviceData('temperature', params);
});

decreaseTempButton.addEventListener('click', function () {
    // Update the temperature on the server
    let params = new URLSearchParams({
        mac: deviceMac,
        temperature: --temperature,
    });
    updateGreeDeviceData('temperature', params);
});

windSpeedSelect.addEventListener('change', function () {
    // Update wind speed on the server
    let params = new URLSearchParams({
        mac: deviceMac,
        fanSpeed: windSpeedSelect.value
    });
    updateGreeDeviceData('fan-speed', params);
});

horizontalSwingSelect.addEventListener('change', function () {
    // Update swing directions on the server
    let params = new URLSearchParams({
        mac: deviceMac,
        horizontalSwingDirection: horizontalSwingSelect.value,
        verticalSwingDirection: verticalSwingSelect.value
    });
    updateGreeDeviceData('swing', params);
});

verticalSwingSelect.addEventListener('change', function () {
    // Update swing directions on the server
    let params = new URLSearchParams({
        mac: deviceMac,
        horizontalSwingDirection: horizontalSwingSelect.value,
        verticalSwingDirection: verticalSwingSelect.value
    });
    updateGreeDeviceData('swing', params);
});

// Select last used device
const lastDeviceMac = Cookies.get(LAST_DEVICE_COOKIE_NAME);
if (lastDeviceMac != null) {
    found = false;
    for (i = 0; i < deviceSelect.length; ++i){
        if (deviceSelect.options[i].value === lastDeviceMac){
            deviceSelect.options[i].selected = true;
            found = true;
            break;
        }
    }
    if (!found) {
        // Invalid cookie, remove it
        Cookies.remove(LAST_DEVICE_COOKIE_NAME);
    }
}

// Initial data fetch
fetchGreeDeviceData();
